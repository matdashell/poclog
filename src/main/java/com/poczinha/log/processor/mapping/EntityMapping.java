package com.poczinha.log.processor.mapping;

import com.poczinha.log.processor.annotation.LogEntity;
import com.poczinha.log.processor.util.Util;
import com.squareup.javapoet.TypeName;

import javax.lang.model.element.Element;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.persistence.Id;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EntityMapping {
    private FieldMapping id;
    private String name;
    private final Element entity;
    private final List<FieldMapping> fields = new ArrayList<>();

    public EntityMapping(Element entity) {
        this.entity = entity;

        setName();

        for (VariableElement element : ElementFilter.fieldsIn(entity.getEnclosedElements())) {
            if (element.getAnnotation(Id.class) != null) {
                setId(element);
            } else if (!Util.isIgnoreField(element)) {
                fields.add(new FieldMapping(element));
            }
        }
    }

    private void setName() {
        LogEntity logEntity = this.entity.getAnnotation(LogEntity.class);
        this.name = Objects.equals(logEntity.name(), "") ? this.entity.getSimpleName().toString() : logEntity.name();
    }

    private void setId(Element element) {
        if (id != null) throw new RuntimeException("Entity " + entity.getSimpleName() + " has more than one id field");
        this.id = new FieldMapping(element);
    }

    public String getEntityName() {
        return entity.getSimpleName().toString();
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

    public String getName() {
        return name;
    }

    public List<FieldMapping> getFields() {
        return fields;
    }
}
