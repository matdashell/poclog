package com.poczinha.log.service;

import com.poczinha.log.bean.Correlation;
import com.poczinha.log.bean.LogColumnCache;
import com.poczinha.log.domain.response.CorrelationModification;
import com.poczinha.log.domain.response.PeriodModification;
import com.poczinha.log.domain.response.data.FieldModification;
import com.poczinha.log.domain.response.data.GroupTypeModifications;
import com.poczinha.log.hibernate.entity.ColumnEntity;
import com.poczinha.log.hibernate.entity.CorrelationEntity;
import com.poczinha.log.hibernate.entity.RegisterEntity;
import com.poczinha.log.hibernate.repository.RegisterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;

@Service
public class RegisterService {

    @Autowired
    private Correlation correlation;

    @Autowired
    private ColumnService columnService;

    @Autowired
    private RegisterRepository registerRepository;

    @Autowired
    private CorrelationService correlationService;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private LogColumnCache logColumnCache;

    public static final String CREATE_TYPE = "C-";
    public static final String DELETE_TYPE = "D-";
    public static final String UPDATE_TYPE = "U-";

    public RegisterEntity processCreate(String field, String newValue) {
        return process(field, CREATE_TYPE, newValue);
    }

    public RegisterEntity processDelete(String field) {
        return process(field, DELETE_TYPE, null);
    }

    public RegisterEntity processUpdate(String field, String newValue) {
        return process(field, UPDATE_TYPE, newValue);
    }

    private RegisterEntity process(String field, String type, String newValue) {
        ColumnEntity column = logColumnCache.get(field);
        if (column == null) {
            column = columnService.columnEntityWithName(field);
            logColumnCache.put(field, column);
        }
        CorrelationEntity correlationEntity = correlation.getCorrelationEntity();
        return new RegisterEntity(correlationEntity, column, newValue, type);
    }

    @Async
    @Transactional
    public void saveAllRegisters(ListIterator<RegisterEntity> registers, CorrelationEntity correlation) {
        if (registers != null) {

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

    public CorrelationModification getAllModificationsByCorrelation(Long correlation) {
        CorrelationModification response = registerRepository.findAllCorrelationModification(correlation);

        if (response == null) return null;

        List<GroupTypeModifications> entities = Optional.ofNullable(registerRepository.findAllGroupTypesByCorrelation(correlation))
                .orElse(Collections.emptyList());

        response.getEntities().addAll(entities);
        Pageable pageable = PageRequest.of(0, 1);
        entities.forEach(entity -> processEntityModifications(correlation, entity, pageable));

        return response;
    }

    private void processEntityModifications(Long correlation, GroupTypeModifications entity, Pageable pageable) {
        List<FieldModification> modifications = Optional.ofNullable(registerRepository.findAllFieldModifications(correlation, entity.getType()))
                .orElse(Collections.emptyList());

        if (!entity.getType().startsWith(CREATE_TYPE)) {
            String typeId = entity.getType().substring(2);
            modifications.forEach(modification -> {
                String lastNewValue = registerRepository.findLasNewValue(
                        modification.getColumn(),
                        correlation,
                        modification.getNewValue(),
                        List.of(CREATE_TYPE + typeId, UPDATE_TYPE + typeId),
                        pageable).getContent().get(0);
                modification.setLastValue(lastNewValue);
            });
        }

        entity.getModifications().addAll(modifications);
    }
}
