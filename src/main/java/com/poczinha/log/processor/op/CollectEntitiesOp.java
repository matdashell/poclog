package com.poczinha.log.processor.op;

import com.poczinha.log.processor.Context;
import com.poczinha.log.processor.mapping.EntityMapping;
import com.poczinha.log.processor.util.PrefixLogger;
import com.poczinha.log.processor.util.Util;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

import static com.poczinha.log.processor.validate.ProcessorValidate.validateEntity;

public class CollectEntitiesOp {

    private final PrefixLogger log = new PrefixLogger(CollectEntitiesOp.class);

    public void execute() {
        for (Element repository : Context.repositories) {
            Element entity = extractEntityOfRepository(repository);

            if (Util.isIgnoreEntity(entity)) continue;

            String repositoryName = repository.getSimpleName().toString();
            String repositoryPackage = Util.getPackageName(repository) + "." + repositoryName;

            EntityMapping mapping = new EntityMapping(entity, repositoryPackage);
            validateEntity(mapping);

            log.debug("mapping: " + mapping.getEntityName());
            Context.mappings.add(mapping);
        }
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
        throw new RuntimeException("Could not extract entity of repository " + repository.getSimpleName());
    }
}
