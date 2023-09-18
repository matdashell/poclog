package com.poczinha.log.hibernate.service;

import com.poczinha.log.bean.Correlation;
import com.poczinha.log.bean.TypeCountManager;
import com.poczinha.log.domain.TypeEnum;
import com.poczinha.log.domain.response.CorrelationModification;
import com.poczinha.log.domain.response.PeriodModification;
import com.poczinha.log.domain.response.data.FieldModification;
import com.poczinha.log.domain.response.data.GroupTypeModifications;
import com.poczinha.log.hibernate.entity.ColumnEntity;
import com.poczinha.log.hibernate.entity.CorrelationEntity;
import com.poczinha.log.hibernate.entity.RegisterEntity;
import com.poczinha.log.hibernate.repository.RegisterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

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
    private TypeCountManager typeCountManager;

    public void registerCreate(String field, String identifier, String newValue) {
        register(field, identifier, typeCountManager.getCreation(), null, newValue);
    }

    public void registerDelete(String field, String identifier, String lastValue) {
        register(field, identifier, typeCountManager.getDeletion(), lastValue, null);
    }

    public void registerUpdate(String field, String identifier, String lastValue, String newValue) {
        register(field, identifier, typeCountManager.getModification(), lastValue, newValue);
    }

    private void register(String field, String identifier, String type, String lastValue, String newValue) {
        CorrelationEntity correlation = this.correlation.getCorrelationEntity(identifier);
        ColumnEntity column = columnService.columnEntityWithName(field);

        RegisterEntity registerEntity = new RegisterEntity(correlation, column, lastValue, newValue, type);

        correlationService.save(correlation);
        registerRepository.save(registerEntity);
    }

    public List<PeriodModification> getAllPeriodModificationBetween(LocalDateTime start, LocalDateTime end) {
        return registerRepository.findAllByDateBetween(start, end);
    }

    public CorrelationModification getAllModificationsByCorrelation(Long correlation) {
        CorrelationModification response = registerRepository.findAllCorrelationModification(correlation);
        List<GroupTypeModifications> entities = registerRepository.findAllGroupTypesByCorrelation(correlation);

        response.getEntities().addAll(entities);

        for (GroupTypeModifications entity : entities) {
            List<FieldModification> modifications = registerRepository.findAllFieldModifications(
                    correlation,
                    entity.getType());

            entity.getModifications().addAll(modifications);
        }

        return response;
    }
}
