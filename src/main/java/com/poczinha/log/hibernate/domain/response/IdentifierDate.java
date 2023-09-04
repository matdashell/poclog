package com.poczinha.log.hibernate.domain.response;

import java.time.LocalDateTime;

public class IdentifierDate {
    private final String identifier;
    private final String entity;
    private final String correlation;
    private final LocalDateTime date;

    public String getIdentifier() {
        return identifier;
    }

    public String getEntity() {
        return entity;
    }

    public String getCorrelation() {
        return correlation;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public IdentifierDate(String identifier, String entity, String correlation, LocalDateTime date) {
        this.identifier = identifier;
        this.entity = entity;
        this.correlation = correlation;
        this.date = date;
    }
}
