package com.poczinha.log.processor.op;

import com.poczinha.log.processor.Context;
import com.poczinha.log.processor.mapping.EntityMapping;
import com.poczinha.log.processor.util.Util;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

import java.util.List;

import static com.poczinha.log.processor.util.Util.log;
import static com.poczinha.log.processor.util.Util.typeMirrorToElement;
import static com.poczinha.log.processor.validate.ProcessorValidate.validateEntity;

public class CollectEntitiesOp {
    public void execute() {
        for (Element repository : Context.repositories) {

            Element entity = extractEntityOfRepository(repository);

            if (Util.isIgnoreEntity(entity)) continue;

            String repositoryPackage = getPackageName(repository) + "." + repository.getSimpleName();
            log("Found repository {} of entity {} in package {}", repository.getSimpleName(), entity.getSimpleName(), repositoryPackage);
            EntityMapping mapping = new EntityMapping(entity, repositoryPackage);
            validateEntity(mapping);

            log("Adding mapping for repository {}", mapping.getRepositoryPackage());
            Context.mappings.add(mapping);
        }
    }

    private String getPackageName(Element element) {
        while (element.getKind() != ElementKind.PACKAGE) {
            element = element.getEnclosingElement();
        }
        PackageElement packageElement = (PackageElement) element;
        return packageElement.getQualifiedName().toString();
    }

    private Element extractEntityOfRepository(Element repository) {
        TypeElement typeElement = (TypeElement) repository;
        for (TypeMirror superInterface : typeElement.getInterfaces()) {
            DeclaredType declaredType = (DeclaredType) superInterface;
            if (declaredType.asElement().toString().startsWith("org.springframework.data.jpa.repository.JpaRepository")) {
                List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();
                if (!typeArguments.isEmpty()) {
                    TypeMirror entity = typeArguments.get(0);
                    return typeMirrorToElement(entity);
                }
            }
        }

        throw new RuntimeException("Could not extract entity of repository " + repository.getSimpleName());
    }
}
