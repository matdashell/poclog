package com.poczinha.log.processor.processor;

import com.poczinha.log.processor.Context;
import com.poczinha.log.processor.mapping.EntityMapping;
import com.poczinha.log.processor.util.Util;
import com.poczinha.log.processor.validate.ValidateUtil;
import org.springframework.stereotype.Repository;

import javax.lang.model.element.Element;
import javax.persistence.Entity;

public class CollectEntitiesProcessor {

    public void execute() {
        for (Element entity : Context.entities) {
            EntityMapping mapping = processEntity(entity);
            Context.mappings.add(mapping);
        }

        ValidateUtil.containsUniquesEntities();
        ValidateUtil.containsUniquesFields();
    }

    private EntityMapping processEntity(Element entity) {
        Element entityOfRepository;
        EntityMapping mapping = null;

        for (Element repository : Context.repositories) {
            entityOfRepository = Util.extractEntityOfRepository(repository);
            if (entityOfRepository.equals(entity)) {
                ValidateUtil.containsAnnotation(entity, Entity.class);
                ValidateUtil.containsAnnotation(repository, Repository.class);

                mapping = new EntityMapping(entity, repository);
                break;
            }
        }

        return mapping;
    }
}
