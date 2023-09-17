package com.poczinha.log.domain.response.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
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
