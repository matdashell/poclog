package com.poczinha.log.service;

import com.poczinha.log.bean.LogAuthVerifier;
import com.poczinha.log.domain.response.CorrelationModification;
import com.poczinha.log.domain.response.PeriodModification;
import com.poczinha.log.domain.response.data.FieldModification;
import com.poczinha.log.domain.response.data.GroupTypeModification;
import com.poczinha.log.domain.response.data.TableModification;
import com.poczinha.log.hibernate.entity.ColumnEntity;
import com.poczinha.log.hibernate.entity.CorrelationEntity;
import com.poczinha.log.hibernate.entity.RegisterEntity;
import com.poczinha.log.hibernate.repository.RegisterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RegisterService {

    @Autowired
    private RegisterRepository registerRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private LogAuthVerifier logAuthVerifier;

    public static final String CREATE_TYPE = "C-";
    public static final String DELETE_TYPE = "D-";
    public static final String UPDATE_TYPE = "U-";

    public RegisterEntity processCreate(ColumnEntity field, String newValue) {
        return new RegisterEntity(field, newValue, CREATE_TYPE);
    }

    public RegisterEntity processDelete(ColumnEntity field) {
        return new RegisterEntity(field, null, DELETE_TYPE);
    }

    public RegisterEntity processUpdate(ColumnEntity field, String newValue) {
        return new RegisterEntity(field, newValue, UPDATE_TYPE);
    }

    @Async
    @Transactional
    public void saveAllRegisters(ListIterator<RegisterEntity> registers, CorrelationEntity correlation) {
        if (registers != null && correlation != null) {

            entityManager.persist(correlation);
            entityManager.setFlushMode(FlushModeType.COMMIT);

            int i = 0;
            while (registers.hasNext()) {
                entityManager.persist(registers.next());

                if (i % 50 == 0) {
                    entityManager.flush();
                    entityManager.clear();
                }
                i++;
            }

            entityManager.flush();
            entityManager.clear();
        }
    }

    public Page<PeriodModification> getAllPeriodModificationBetween(LocalDateTime start, LocalDateTime end, int page, int size) {
        return registerRepository.findAllByDateBetween(start, end, PageRequest.of(page, size));
    }

    public CorrelationModification getAllModificationsByCorrelation(Long correlationId) {
        CorrelationModification correlation = registerRepository.findAllCorrelationModification(correlationId)
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Correlation not found"));

        List<TableModification> tables = registerRepository.findAllTablesByCorrelation(correlationId);
        tables.forEach(table -> processTableModifications(correlationId, table));

        correlation.getTableModifications().addAll(tables);
        return correlation;
    }

    private void processTableModifications(Long correlationId, TableModification table) {
        List<GroupTypeModification> groupTypes = registerRepository.findAllGroupTypesByCorrelationAndTable(correlationId, table.getTableName());
        groupTypes.forEach(groupType -> processEntityModifications(correlationId, table.getTableName(), groupType));

        table.getGroups().addAll(groupTypes);
    }

    private void processEntityModifications(Long correlation, String tableName, GroupTypeModification group) {
        List<FieldModification> modifications = registerRepository.findAllFieldModifications(correlation, tableName, group.getType())
                .stream()
                .filter(field -> field.getRole() != null || logAuthVerifier.verify(field.getRole()))
                .collect(Collectors.toList());

        if (!group.getType().startsWith(CREATE_TYPE)) {
            String typeId = group.getType().substring(2);
            updateFieldModificationsWithLastValue(modifications, typeId, correlation, tableName);
        }

        group.getModifications().addAll(modifications);
    }

    private void updateFieldModificationsWithLastValue(List<FieldModification> modifications, String typeId, Long correlation, String tableName) {
        List<String> types = Arrays.asList(CREATE_TYPE + typeId, UPDATE_TYPE + typeId);
        modifications.forEach(modification -> {
            String lastNewValue = findLastValueForModification(correlation, tableName, modification, types);
            modification.setLastValue(lastNewValue);
        });
    }

    private String findLastValueForModification(Long correlation, String tableName, FieldModification modification, List<String> types) {
        return registerRepository.findFieldLastValueFromModification(
                modification.getField(),
                correlation,
                tableName,
                modification.getNewValue(),
                types,
                PageRequest.of(0, 1)
        ).getContent().get(0);
    }
}
