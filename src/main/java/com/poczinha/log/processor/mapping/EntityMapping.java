package com.poczinha.log.processor.mapping;

import com.poczinha.log.processor.util.Util;
import com.poczinha.log.processor.validate.ValidateUtil;
import com.squareup.javapoet.TypeName;
import org.springframework.data.mapping.MappingException;

import javax.lang.model.element.Element;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.persistence.Id;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EntityMapping {
    private final FieldMapping id;
    private final String repositoryPackage;
    private final Element entity;
    private final String name;
    private final List<FieldMapping> fields;

    public EntityMapping(Element entity, String repositoryPackage, String namedTable) {
        validateNonNullArgs(entity, repositoryPackage, namedTable);

        this.entity = entity;
        this.name = namedTable;
        this.fields = new ArrayList<>();
        this.repositoryPackage = repositoryPackage;

        this.id = processFields();
    }

    private void validateNonNullArgs(Element entity, String repositoryPackage, String namedTable) {
        Objects.requireNonNull(entity, "Entity must not be null");
        Objects.requireNonNull(repositoryPackage, "Repository package must not be null");
        Objects.requireNonNull(namedTable, "Named table must not be null");
    }

    private FieldMapping processFields() {
        FieldMapping idField = null;

        for (VariableElement element : ElementFilter.fieldsIn(entity.getEnclosedElements())) {
            if (element.getAnnotation(Id.class) != null) {
                ValidateUtil.validateSingleIdField(idField, element);
                idField = new FieldMapping(element);
            } else if (!Util.isIgnoreField(element)) {
                ValidateUtil.validField(element, this.entity);
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
        return repositoryPackage;
    }

    public String getName() {
        return name;
    }
}
