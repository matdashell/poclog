package com.poczinha.log.hibernate.service;

import com.poczinha.log.hibernate.domain.Correlation;
import com.poczinha.log.hibernate.domain.TypeEnum;
import com.poczinha.log.hibernate.domain.response.CorrelationModification;
import com.poczinha.log.hibernate.domain.response.PeriodModification;
import com.poczinha.log.hibernate.domain.response.data.EntityModification;
import com.poczinha.log.hibernate.domain.response.data.FieldModification;
import com.poczinha.log.hibernate.entity.RegisterEntity;
import com.poczinha.log.hibernate.repository.RegisterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class RegisterService {

    @Autowired
    private RegisterRepository registerRepository;

    @Autowired
    private Correlation correlation;

    public void registerCreate(String entity, String field, String identifier, String newValue) {
        register(entity, field, identifier, TypeEnum.C, null, newValue);
    }

    public void registerUpdate(String entity, String field, String identifier, String lastValue, String newValue) {
        register(entity, field, identifier, TypeEnum.U, lastValue, newValue);
    }

    public void registerDelete(String entity, String field, String identifier, String lastValue) {
        register(entity, field, identifier, TypeEnum.D, lastValue, null);
    }

    private void register(String entity, String field, String identifier, TypeEnum type, String lastValue, String newValue) {
        RegisterEntity registerEntity = new RegisterEntity(entity, field, identifier, type, lastValue, newValue, correlation.getDate(), correlation.getId());
        registerRepository.save(registerEntity);
    }

    public List<PeriodModification> getAllPeriodModificationBetween(LocalDateTime start, LocalDateTime end) {
        return registerRepository.getAllPeriodModificationBetween(start, end);
    }

    public CorrelationModification getAllModificationsByCorrelation(String correlation) {
        String identifier = registerRepository.getIdentifierByCorrelation(correlation);

        CorrelationModification response = new CorrelationModification(correlation, identifier);

        List<EntityModification> entities = registerRepository.getAllModificationsByCorrelation(correlation);

        for (EntityModification entity : entities) {
            List<FieldModification> fields = registerRepository.getAllModificationsByCorrelationAndEntityAndType(
                    correlation,
                    entity.getEntity(),
                    entity.getType());

            entity.getModifications().addAll(fields);
        }

        response.getEntities().addAll(entities);
        return response;
    }
}
