package com.poczinha.log.hibernate.service;

import com.poczinha.log.hibernate.domain.Correlation;
import com.poczinha.log.hibernate.domain.TypeEnum;
import com.poczinha.log.hibernate.domain.response.CorrelationModification;
import com.poczinha.log.hibernate.domain.response.PeriodModification;
import com.poczinha.log.hibernate.domain.response.data.EntityModification;
import com.poczinha.log.hibernate.domain.response.data.FieldModification;
import com.poczinha.log.hibernate.entity.ColumnEntity;
import com.poczinha.log.hibernate.entity.CorrelationEntity;
import com.poczinha.log.hibernate.entity.RegisterEntity;
import com.poczinha.log.hibernate.entity.TableEntity;
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
    private TableService tableService;

    @Autowired
    private RegisterRepository registerRepository;

    @Autowired
    private CorrelationService correlationService;

    public void registerCreate(TableEntity table, String field, String identifier, String newValue) {
        register(table, field, identifier, TypeEnum.C, null, newValue);
    }

    public void registerDelete(TableEntity table, String field, String identifier, String lastValue) {
        register(table, field, identifier, TypeEnum.D, lastValue, null);
    }

    public void registerUpdate(TableEntity table, String field, String identifier, String lastValue, String newValue) {
        register(table, field, identifier, TypeEnum.U, lastValue, newValue);
    }

    private void register(TableEntity table, String field, String identifier, TypeEnum type, String lastValue, String newValue) {
        CorrelationEntity correlation = this.correlation.getCorrelationEntity();
        ColumnEntity column = columnService.columnEntityWithName(field);

        RegisterEntity registerEntity = new RegisterEntity(correlation, column, table, identifier, lastValue, newValue, type);

        correlationService.save(correlation);
        registerRepository.save(registerEntity);
    }

    public List<PeriodModification> getAllPeriodModificationBetween(LocalDateTime start, LocalDateTime end) {
        return registerRepository.findAllByDateBetween(start, end);
    }

    public CorrelationModification getAllModificationsByCorrelation(Long correlation) {
        CorrelationModification response = registerRepository.findAllCorrelationModification(correlation);
        List<EntityModification> entities = registerRepository.findAllEntityModifications(correlation);

        response.getEntities().addAll(entities);

        for (EntityModification entity : entities) {
            List<FieldModification> modifications = registerRepository.findAllFieldModifications(
                    correlation,
                    entity.getType(),
                    entity.getEntity());

            entity.getModifications().addAll(modifications);
        }

        return response;
    }
}
