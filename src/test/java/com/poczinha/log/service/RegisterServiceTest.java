package com.poczinha.log.service;

import com.poczinha.log.bean.LogAuthVerifier;
import com.poczinha.log.domain.response.CorrelationModification;
import com.poczinha.log.domain.response.PeriodModification;
import com.poczinha.log.domain.response.data.FieldModification;
import com.poczinha.log.domain.response.data.GroupTypeModification;
import com.poczinha.log.domain.response.data.TableModification;
import com.poczinha.log.hibernate.entity.LogColumnEntity;
import com.poczinha.log.hibernate.entity.LogCorrelationEntity;
import com.poczinha.log.hibernate.entity.LogRegisterEntity;
import com.poczinha.log.hibernate.repository.RegisterRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.client.HttpClientErrorException;

import javax.persistence.EntityManager;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegisterServiceTest {

    @Spy
    @InjectMocks
    private RegisterService registerService;

    @Mock
    private EntityManager entityManager;

    @Mock
    private LogAuthVerifier logAuthVerifier;

    @Mock
    private RegisterRepository registerRepository;

    private final String newValue = "newValue";
    private final LogColumnEntity logColumnEntity = new LogColumnEntity();

    @Test
    @DisplayName("Should process create correctly")
    void shouldProcessCreateCorrectly() {
        LogRegisterEntity result = registerService.processCreate(logColumnEntity, newValue);

        assertEquals(logColumnEntity, result.getColumn());
        assertEquals(newValue, result.getNewValue());
        assertEquals("C", result.getType());
    }

    @Test
    @DisplayName("Should process delete correctly")
    void shouldProcessDeleteCorrectly() {
        LogRegisterEntity result = registerService.processDelete(logColumnEntity);

        assertEquals(logColumnEntity, result.getColumn());
        assertNull(result.getNewValue());
        assertEquals("D", result.getType());
    }

    @Test
    @DisplayName("Should process update correctly")
    void shouldProcessUpdateCorrectly() {
        LogRegisterEntity result = registerService.processUpdate(logColumnEntity, newValue);

        assertEquals(logColumnEntity, result.getColumn());
        assertEquals(newValue, result.getNewValue());
        assertEquals("U", result.getType());
    }

    @Test
    @DisplayName("Should save all registers correctly")
    void shouldSaveAllRegistersCorrectly() {
        ListIterator<LogRegisterEntity> regs = List.of(new LogRegisterEntity()).listIterator();
        LogCorrelationEntity correlation = new LogCorrelationEntity();

        registerService.saveAllRegisters(regs, correlation);

        verify(entityManager).persist(correlation);

        verify(entityManager, times(2)).flush();
        verify(entityManager, times(2)).clear();
    }

    @Test
    @DisplayName("Not should save all when registers is null")
    void notShouldSaveAllWhenRegistersIsNull() {
        registerService.saveAllRegisters(null, new LogCorrelationEntity());

        verify(entityManager, never()).persist(new LogCorrelationEntity());
    }

    @Test
    @DisplayName("Not should save all when registers is empty")
    void notShouldSaveAllWhenRegistersIsEmpty() {
        ListIterator<LogRegisterEntity> registers = new ArrayList<LogRegisterEntity>().listIterator();
        registerService.saveAllRegisters(registers, new LogCorrelationEntity());

        verify(entityManager, never()).persist(new LogCorrelationEntity());
    }

    @Test
    @DisplayName("Should get all period modifications by period correctly")
    void shouldGetAllPeriodModificationsByPeriodCorrectly() {
        int page = 0;
        int size = 1;
        LocalDateTime now = LocalDateTime.now();
        PeriodModification periodModification = new PeriodModification(
                "id",
                0L,
                now);

        when(registerRepository.findAllByDateBetween(any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(periodModification)));

        PeriodModification result = registerService.getAllPeriodModificationBetween(now, now, page, size)
                .getContent().get(page);

        verify(registerRepository).findAllByDateBetween(now, now, PageRequest.of(page, size));

        assertEquals(periodModification, result);
    }

    @Test
    @DisplayName("Should get all period modifications by correlation correctly")
    void shouldGetAllPeriodModificationsByCorrelationCorrectly() {
        CorrelationModification response = new CorrelationModification(null, null, null);
        List<TableModification> tableModifications = List.of(new TableModification("table"));

        when(registerRepository.findAllCorrelationModification(anyLong()))
                .thenReturn(Optional.of(response));
        when(registerRepository.findAllTablesByCorrelation(anyLong()))
                .thenReturn(tableModifications);
        doNothing().when(registerService).processTableModifications(anyLong(), any());

        CorrelationModification result = registerService.getAllModificationsByCorrelation(0L);

        verify(registerRepository).findAllCorrelationModification(0L);
        verify(registerRepository).findAllTablesByCorrelation(0L);
        verify(registerService).processTableModifications(0L, tableModifications.get(0));

        assertEquals(response, result);
        assertEquals(tableModifications, response.getTableModifications());
    }

    @Test
    @DisplayName("Should not get all period modifications by correlation when not found")
    void shouldNotGetAllPeriodModificationsByCorrelationWhenNotFound() {
        when(registerRepository.findAllCorrelationModification(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(
                HttpClientErrorException.class,
                () -> registerService.getAllModificationsByCorrelation(0L),
                "Correlation not found"
        );
    }

    @Test
    @DisplayName("Should process table modifications correctly")
    void shouldProcessTableModificationsCorrectly() {
        TableModification tableModification = new TableModification("table");
        List<GroupTypeModification> groupTypeModification = List.of(new GroupTypeModification("type"));

        when(registerRepository.findAllGroupTypesByCorrelationAndTable(anyLong(), anyString()))
                .thenReturn(groupTypeModification);
        doNothing().when(registerService).processEntityModifications(anyLong(), anyString(), any());

        registerService.processTableModifications(0L, tableModification);

        verify(registerRepository).findAllGroupTypesByCorrelationAndTable(0L, "table");
        verify(registerService).processEntityModifications(0L, "table", groupTypeModification.get(0));

        assertEquals(groupTypeModification, tableModification.getGroupsModifications());
    }

    @Test
    @DisplayName("Should process entity modifications correctly")
    void shouldProcessEntityModificationsCorrectly() {
        GroupTypeModification groupTypeModification = new GroupTypeModification("U3");
        List<FieldModification> fieldModifications = List.of(
                new FieldModification("field", "newValue", "role"),
                new FieldModification("field", "newValue", null)
        );

        when(registerRepository.findAllFieldModifications(anyLong(), anyString(), anyString()))
                .thenReturn(fieldModifications);
        when(logAuthVerifier.verify(anyString()))
                .thenReturn(true);
        doNothing().when(registerService).updateFieldModificationsWithLastValue(anyList(), anyString(), anyLong(), anyString());

        registerService.processEntityModifications(0L, "table", groupTypeModification);

        verify(registerRepository).findAllFieldModifications(0L, "table", groupTypeModification.getType());
        verify(logAuthVerifier).verify(anyString());
        verify(registerService).updateFieldModificationsWithLastValue(fieldModifications, "3", 0L, "table");

        assertEquals(fieldModifications, groupTypeModification.getModifications());
    }

    @Test
    @DisplayName("Should update field modifications with last value correctly")
    void shouldUpdateFieldModificationsWithLastValueCorrectly() {
        List<FieldModification> fieldModifications = List.of(
                new FieldModification("field", "newValue", null)
        );
        String lastValue = "lastValue";

        doReturn(lastValue).when(registerService)
                .findLastValueForModification(anyLong(), anyString(), any(), anyList(), any());

        registerService.updateFieldModificationsWithLastValue(fieldModifications, "3", 0L, "table");

        verify(registerService).findLastValueForModification(0L, "table", fieldModifications.get(0), List.of("C3", "U3"), PageRequest.of(0, 1));

        assertEquals("lastValue", fieldModifications.get(0).getLastValue());
    }

    @Test
    @DisplayName("Should find last value for modification correctly")
    void shouldFindLastValueForModificationCorrectly() {
        Page<String> page = new PageImpl<>(List.of("lastValue"));
        List<String> types = List.of("C3", "U3");
        FieldModification fieldModification = new FieldModification("field", "newValue", null);

        when(registerRepository.findFieldLastValueFromModification(anyList(), anyString(), anyLong(), anyString(), anyString(), any()))
                .thenReturn(page);

        String result = registerService.findLastValueForModification(0L, "table", fieldModification, types, PageRequest.of(0, 1));

        verify(registerRepository).findFieldLastValueFromModification(types, "table", 0L, "field", "newValue", PageRequest.of(0, 1));

        assertEquals("lastValue", result);
    }

    @Test
    @DisplayName("Should not find last value for modification when page is empty")
    void shouldNotFindLastValueForModificationWhenPageIsEmpty() {
        Page<String> page = Page.empty();
        List<String> types = List.of("C3", "U3");
        FieldModification fieldModification = new FieldModification("field", "newValue", null);

        when(registerRepository.findFieldLastValueFromModification(anyList(), anyString(), anyLong(), anyString(), anyString(), any()))
                .thenReturn(page);

        String result = registerService.findLastValueForModification(0L, "table", fieldModification, types, PageRequest.of(0, 1));

        verify(registerRepository).findFieldLastValueFromModification(types, "table", 0L, "field", "newValue", PageRequest.of(0, 1));

        assertNull(result);
    }
}