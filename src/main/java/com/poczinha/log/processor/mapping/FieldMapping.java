package com.poczinha.log.processor.mapping;

import com.poczinha.log.annotation.LogField;
import com.poczinha.log.processor.util.Util;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;
import java.util.Objects;

public class FieldMapping {
    private final Element field;
    private final String fieldSnakeCase;
    private final String name;

    public FieldMapping(Element field) {
        Objects.requireNonNull(field, "Field must not be null");

        this.field = field;
        this.name = determineName();
        this.fieldSnakeCase = Util.toUpperSnakeCase(this.field.getSimpleName().toString());
    }

    private String determineName() {
        LogField logEntity = this.field.getAnnotation(LogField.class);
        if (logEntity == null || Objects.equals(logEntity.name(), "")) {
            return this.field.getSimpleName().toString();
        } else {
            return logEntity.name();
        }
    }

    public String getName() {
        return name;
    }

    public TypeMirror asType() {
        return field.asType();
    }

    public String getFieldSimpleName() {
        return field.getSimpleName().toString();
    }

    public String getFieldSnakeCase() {
        return fieldSnakeCase;
    }

    public String getAccess() {
        String simpleName = field.getSimpleName().toString();
        return "get" + simpleName.substring(0, 1).toUpperCase() + simpleName.substring(1) + "()";
    }

    public String getNameAccess() {
        return getAccess().replace("()", "");
    }
}
