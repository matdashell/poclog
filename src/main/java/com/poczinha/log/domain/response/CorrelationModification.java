package com.poczinha.log.domain.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.poczinha.log.domain.response.data.EntityModification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@JsonPropertyOrder({"identifier", "correlation", "date", "entities"})
public class CorrelationModification {
    private final String identifier;
    private final Long correlation;
    private final LocalDateTime date;

    private final List<EntityModification> entities = new ArrayList<>();

    public CorrelationModification(String identifier, Long correlation, LocalDateTime date) {
        this.identifier = identifier;
        this.correlation = correlation;
        this.date = date;
    }

    public Long getCorrelation() {
        return correlation;
    }

    public String getIdentifier() {
        return identifier;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public List<EntityModification> getEntities() {
        return entities;
    }
}
