package com.poczinha.log.domain.response.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"field", "lastValue", "newValue"})
public class FieldModification {
    private final String field;
    private String lastValue;
    private final String newValue;

    @JsonIgnore
    private final String role;

    public FieldModification(String field, String newValue, String role) {
        this.field = field;
        this.newValue = newValue;
        this.role = role;
    }

    public String getField() {
        return field;
    }

    public String getLastValue() {
        return lastValue;
    }

    public void setLastValue(String lastValue) {
        this.lastValue = lastValue;
    }

    public String getNewValue() {
        return newValue;
    }

    public String getRole() {
        return role;
    }
}
