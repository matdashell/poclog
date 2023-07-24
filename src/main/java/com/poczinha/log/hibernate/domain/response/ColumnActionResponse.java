package com.poczinha.log.hibernate.domain.response;

import java.time.LocalDateTime;

public class ColumnActionResponse {
    private final String name;
    private final String type;
    private final String lastValue;
    private final String newValue;
    private final LocalDateTime createdAt;

    public ColumnActionResponse(String name, String type, String lastValue, String newValue, LocalDateTime createdAt) {
        this.name = name;
        this.type = type;
        this.lastValue = lastValue;
        this.newValue = newValue;
        this.createdAt = createdAt;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getLastValue() {
        return lastValue;
    }

    public String getNewValue() {
        return newValue;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
