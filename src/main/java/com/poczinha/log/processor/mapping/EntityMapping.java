package com.poczinha.log.processor.mapping;

import com.poczinha.log.processor.util.Util;
import com.poczinha.log.processor.validate.ValidateUtil;
import com.squareup.javapoet.TypeName;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.persistence.Id;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EntityMapping {
    private final String name;
    private final Element entity;
    private final FieldMapping id;
    private final Element repository;
    private final List<FieldMapping> fields;

    public EntityMapping(Element entity, Element entityRepository) {
        validateNonNullArgs(entity, entityRepository);

        this.entity = entity;
        this.fields = new ArrayList<>();
        this.repository = entityRepository;
        this.name = Util.extractEntityName(entity);

        this.id = processFields();
        ValidateUtil.validateMapping(this);
    }

    private void validateNonNullArgs(Element entity, Element repositoryPackage) {
        Objects.requireNonNull(entity, "Entity must not be null");
        Objects.requireNonNull(repositoryPackage, "Repository must not be null");
    }

    private FieldMapping processFields() {
        FieldMapping idField = null;

        for (VariableElement element : Util.extractAllFields((TypeElement) entity)) {
            if (element.getAnnotation(Id.class) != null) {
                ValidateUtil.validateSingleIdField(idField, element);
                idField = new FieldMapping(element);
            } else if (!Util.isIgnoreField(element)) {
                fields.add(new FieldMapping(element));
            }
        }
        return idField;
    }

    public String getEntityName() {
        return entity.getSimpleName().toString();
    }

    public String getEntitySimpleName() {
        return getEntityName().substring(0, 1).toLowerCase() + getEntityName().substring(1);
    }

    public TypeMirror asType() {
        return entity.asType();
    }

    public TypeName getEntityTypeName() {
        return TypeName.get(this.asType());
    }

    public FieldMapping getId() {
        return id;
    }

    public List<FieldMapping> getFields() {
        return fields;
    }

    public String getRepositoryPackage() {
        return Util.getPackageName(repository);
    }

    public String getName() {
        return name;
    }
}
