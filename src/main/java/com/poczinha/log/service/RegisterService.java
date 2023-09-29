package com.poczinha.log.service;

import com.poczinha.log.bean.Correlation;
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
    private List<RegisterEntity> registerEntities;

    public static final String CREATE_TYPE = "C-";
    public static final String DELETE_TYPE = "D-";
    public static final String UPDATE_TYPE = "U-";

    public RegisterEntity processCreate(String field, String newValue) {
        return process(field, CREATE_TYPE, null, newValue);
    }

    public RegisterEntity processDelete(String field, String lastValue) {
        return process(field, DELETE_TYPE, lastValue, null);
    }

    public RegisterEntity processUpdate(String field, String lastValue, String newValue) {
        return process(field, UPDATE_TYPE, lastValue, newValue);
    }

    private RegisterEntity process(String field, String type, String lastValue, String newValue) {
        CorrelationEntity correlationEntity = correlation.getCorrelationEntity();
        ColumnEntity column = columnService.columnEntityWithName(field);
        return new RegisterEntity(correlationEntity, column, lastValue, newValue, type);
    }

    public void saveAll() {
        if (!registerEntities.isEmpty()) {
            correlationService.save(correlation.getCorrelationEntity());
            registerRepository.saveAll(registerEntities);
        }
    }

    public List<PeriodModification> getAllPeriodModificationBetween(LocalDateTime start, LocalDateTime end) {
        return registerRepository.findAllByDateBetween(start, end);
    }

    public CorrelationModification getAllModificationsByCorrelation(Long correlation) {
        CorrelationModification response = registerRepository.findAllCorrelationModification(correlation);

        if (response == null) {
            return null;
        } else {
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
}
