package com.poczinha.log.service;

import com.poczinha.log.domain.response.PeriodModification;
import com.poczinha.log.hibernate.entity.LogCorrelationEntity;
import com.poczinha.log.hibernate.repository.CorrelationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CorrelationServiceTest {

    @Spy
    @InjectMocks
    private CorrelationService correlationService;

    @Mock
    private EntityManager entityManager;

    @Mock
    private CorrelationRepository correlationRepository;

    @Mock
    private TypedQuery<PeriodModification> periodModificationTypedQuery;

    @Mock
    private TypedQuery<Long> longTypedQuery;

    @Test
    @DisplayName("Should save correlation")
    void shouldSaveCorrelation() {
        LogCorrelationEntity entity = new LogCorrelationEntity();
        correlationService.save(entity);

        verify(correlationRepository).save(entity);
    }

    @Test
    @DisplayName("Should return empty page when values is empty")
    void shouldReturnEmptyPageWhenValuesIsEmpty() {
        Page<PeriodModification> result = correlationService.findAllByIdentifier(List.of(), 0, 10);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should find all by identifier correctly")
    void shouldFindAllByIdentifierCorrectly() {
        long total = 10;
        PeriodModification periodModification = new PeriodModification(
                "id",
                0L,
                LocalDateTime.now()
        );

        doReturn("select").when(correlationService).createSelectQuery(anyList());
        doReturn("select").when(correlationService).createCountQuery(anyList());

        doReturn(periodModificationTypedQuery)
                .when(entityManager).createQuery(anyString(), eq(PeriodModification.class));
        doReturn(longTypedQuery)
                .when(entityManager).createQuery(anyString(), eq(Long.class));

        when(longTypedQuery.getSingleResult())
                .thenReturn(total);
        when(periodModificationTypedQuery.getResultList())
                .thenReturn(List.of(periodModification));

        Page<PeriodModification> result = correlationService.findAllByIdentifier(List.of("id"), 0, 10);

        assertEquals(1, result.getContent().size());
        assertEquals(periodModification, result.getContent().get(0));
    }

    @Test
    @DisplayName("Should create count query correctly")
    void shouldCreateCountQueryCorrectly() {
        String result = correlationService.createCountQuery(List.of("id"));

        assertEquals("SELECT count(c) FROM LogCorrelationEntity c WHERE LOWER(c.identifier) LIKE :param0", result);
    }

    @Test
    @DisplayName("Should create select query correctly")
    void shouldCreateSelectQueryCorrectly() {
        String result = correlationService.createSelectQuery(List.of("id"));

        assertEquals("SELECT new com.poczinha.log.domain.response.PeriodModification( c.identifier, c.id, c.date) FROM LogCorrelationEntity c WHERE LOWER(c.identifier) LIKE :param0 ORDER BY c.date DESC", result);
    }
}