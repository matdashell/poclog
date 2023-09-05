package com.poczinha.log.hibernate.domain.response;

import com.poczinha.log.hibernate.domain.response.data.EntityModification;

import java.util.ArrayList;
import java.util.List;

public class CorrelationModification {
    private final String correlation;
    private final String identifier;

    private final List<EntityModification> entities = new ArrayList<>();

    public CorrelationModification(String correlation, String identifier) {
        this.correlation = correlation;
        this.identifier = identifier;
    }

    public String getCorrelation() {
        return correlation;
    }

    public String getIdentifier() {
        return identifier;
    }

    public List<EntityModification> getEntities() {
        return entities;
    }
}
