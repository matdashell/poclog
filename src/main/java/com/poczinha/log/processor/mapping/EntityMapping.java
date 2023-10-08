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

    public EntityMapping(Element entity, String repositoryPackage, String nammedTable) {
        Objects.requireNonNull(entity, "Entity must not be null");
        Objects.requireNonNull(repositoryPackage, "Repository package must not be null");
        Objects.requireNonNull(nammedTable, "Nammed table must not be null");

        this.entity = entity;
        this.name = nammedTable;
        this.fields = new ArrayList<>();
        this.repositoryPackage = repositoryPackage;

        FieldMapping idField = null;
        for (VariableElement element : ElementFilter.fieldsIn(entity.getEnclosedElements())) {
            if (element.getAnnotation(Id.class) != null) {
                if (this.getId() != null) {
                    throw new MappingException("Entity ´" + element.getSimpleName() + "´ has more than one id field");
                }
                idField = new FieldMapping(element);

            } else if (!Util.isIgnoreField(element)) {
                ValidateUtil.validField(element, this.entity);
                fields.add(new FieldMapping(element));
            }
        }
        this.id = idField;
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
