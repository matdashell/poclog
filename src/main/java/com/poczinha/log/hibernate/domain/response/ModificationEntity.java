package com.poczinha.log.hibernate.domain.response;

public class ModificationEntity {
    private final String field;
    private final String lastValue;
    private final String newValue;

    public String getField() {
        return field;
    }

    public String getLastValue() {
        return lastValue;
    }

    public String getNewValue() {
        return newValue;
    }

    public ModificationEntity(String field, String lastValue, String newValue) {
        this.field = field;
        this.lastValue = lastValue;
        this.newValue = newValue;
    }
}
