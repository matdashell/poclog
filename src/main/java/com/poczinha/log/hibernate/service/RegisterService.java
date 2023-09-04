package com.poczinha.log.hibernate.service;

import com.poczinha.log.hibernate.domain.Correlation;
import com.poczinha.log.hibernate.domain.TypeEnum;
import com.poczinha.log.hibernate.domain.response.IdentifierDate;
import com.poczinha.log.hibernate.domain.response.ModificationEntity;
import com.poczinha.log.hibernate.domain.response.ModificationIdentifier;
import com.poczinha.log.hibernate.domain.response.TypeDate;
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

    public List<IdentifierDate> getAllIdentifiersModifiedIn(LocalDateTime start, LocalDateTime end) {
        return registerRepository.findAllByDateBetween(start, end);
    }

    public List<ModificationIdentifier> getAllModificationsByIdentifierBetween(String identifier, LocalDateTime start, LocalDateTime end) {
        List<TypeDate> typeDates = registerRepository.findAllDistinctsByCorrelation(identifier, start, end);
        List<ModificationIdentifier> responses = new ArrayList<>();

        for (TypeDate typeDate : typeDates) {
            ModificationIdentifier modificationIdentifier = new ModificationIdentifier(
                    typeDate.getType(),
                    typeDate.getEntity(),
                    typeDate.getCorrelation(),
                    typeDate.getDate());

            List<ModificationEntity> modificationEntities = registerRepository.findAllByTypeAndDateAndIdentifier(
                    typeDate.getType(),
                    typeDate.getDate(),
                    identifier);

            modificationIdentifier.setModifications(modificationEntities);

            responses.add(modificationIdentifier);
        }

        return responses;
    }

    public List<ModificationIdentifier> getAllModificationsByCorrelation(String correlation) {
        List<TypeDate> typeDates = registerRepository.findAllDistinctsByCorrelation(correlation);
        List<ModificationIdentifier> responses = new ArrayList<>();

        for (TypeDate typeDate : typeDates) {
            ModificationIdentifier modificationIdentifier = new ModificationIdentifier(
                    typeDate.getType(),
                    typeDate.getEntity(),
                    typeDate.getCorrelation(),
                    typeDate.getDate());

            List<ModificationEntity> modificationEntities = registerRepository.findAllByTypeAndEntityAndCorrelation(
                    typeDate.getType(),
                    typeDate.getEntity(),
                    typeDate.getCorrelation());

            modificationIdentifier.setModifications(modificationEntities);

            responses.add(modificationIdentifier);
        }

        return responses;
    }
}
