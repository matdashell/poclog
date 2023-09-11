package com.poczinha.log.hibernate.domain.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.time.LocalDateTime;

@JsonPropertyOrder({"identifier", "correlation", "date"})
public class PeriodModification {
    private final String identifier;
    private final Long correlation;
    private final LocalDateTime date;

    public PeriodModification(String identifier, Long correlation, LocalDateTime date) {
        this.identifier = identifier;
        this.correlation = correlation;
        this.date = date;
    }

    public String getIdentifier() {
        return identifier;
    }

    public Long getCorrelation() {
        return correlation;
    }

    public LocalDateTime getDate() {
        return date;
    }
}
