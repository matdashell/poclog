package com.poczinha.log.processor.op.impl;

import com.poczinha.log.processor.Context;
import com.poczinha.log.processor.mapping.EntityMapping;
import com.poczinha.log.processor.op.CollectEntitiesOp;
import com.poczinha.log.processor.util.PrefixLogger;
import com.poczinha.log.processor.util.Util;
import com.poczinha.log.processor.validate.ValidateUtil;
import org.springframework.data.mapping.MappingException;
import org.springframework.stereotype.Repository;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.persistence.Entity;

public class CollectEntitiesOpImpl implements CollectEntitiesOp {

    private final PrefixLogger log = new PrefixLogger(CollectEntitiesOpImpl.class);

    @Override
    public void execute() {
        for (Element repository : Context.repositories) {
            try {
                EntityMapping mapping = processRepository(repository);
                Context.mappings.add(mapping);
                log.debug("Mapping added: " + mapping.getEntityName());
            } catch (Exception e) {
                log.error("Error processing repository {} - {}" + repository.getSimpleName(), e, e.getMessage());
                throw e;
            }
        }

        ValidateUtil.containsUniquesFields();
    }

    private EntityMapping processRepository(Element repository) {
        Element entity = extractEntityOfRepository(repository);

        ValidateUtil.containsAnnotation(entity, Entity.class);
        ValidateUtil.containsAnnotation(repository, Repository.class);

        String repositoryPackage = Util.getPackageName(repository);
        return new EntityMapping(entity, repositoryPackage);
    }

    private Element extractEntityOfRepository(Element repository) {
        TypeElement typeElement = (TypeElement) repository;
        for (TypeMirror superInterface : typeElement.getInterfaces()) {
            if (Util.isSpringDataInterface(superInterface)) {
                TypeMirror entity = Util.getFirstTypeArgument((DeclaredType) superInterface);
                if (entity != null) {
                    return Util.typeMirrorToElement(entity);
                }
            }
        }
        throw new MappingException("Could not extract entity of repository " + repository.getSimpleName());
    }
}
