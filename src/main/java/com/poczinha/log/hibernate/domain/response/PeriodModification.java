package com.poczinha.log.hibernate.domain.response;

import java.time.LocalDateTime;

public class PeriodModification {
    private final String identifier;
    private final String correlation;
    private final LocalDateTime date;

    public PeriodModification(String identifier, String correlation, LocalDateTime date) {
        this.identifier = identifier;
        this.correlation = correlation;
        this.date = date;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getCorrelation() {
        return correlation;
    }

    public LocalDateTime getDate() {
        return date;
    }
}
