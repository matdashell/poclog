package com.poczinha.log.processor.mapping;

import com.poczinha.log.processor.annotation.LogField;
import com.poczinha.log.processor.util.Util;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;
import java.util.Objects;

public class FieldMapping {
    private final Element field;
    private String name;

    public FieldMapping(Element field) {
        this.field = field;
        setName();
    }

    private void setName() {
        LogField logEntity = this.field.getAnnotation(LogField.class);
        if (logEntity == null) this.name = this.field.getSimpleName().toString();
        else this.name = Objects.equals(logEntity.name(), "") ? this.field.getSimpleName().toString() : logEntity.name();
    }

    public String getName() {
        return name;
    }

    public TypeMirror asType() {
        return field.asType();
    }

    public Element getField() {
        return field;
    }

    public String getAccess() {
        return Util.getAccessOf(field.getSimpleName().toString());
    }
}
