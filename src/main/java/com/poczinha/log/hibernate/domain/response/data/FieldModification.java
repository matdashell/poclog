package com.poczinha.log.hibernate.domain.response.data;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"field", "lastValue", "newValue"})
public class FieldModification {
    private final String field;
    private final String lastValue;
    private final String newValue;

    public FieldModification(String field, String lastValue, String newValue) {
        this.field = field;
        this.lastValue = lastValue;
        this.newValue = newValue;
    }

    public String getColumn() {
        return field;
    }

    public String getOldValue() {
        return lastValue;
    }

    public String getNewValue() {
        return newValue;
    }
}
