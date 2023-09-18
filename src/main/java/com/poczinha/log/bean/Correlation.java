package com.poczinha.log.bean;

import com.poczinha.log.hibernate.entity.CorrelationEntity;

public class Correlation {
    private final CorrelationEntity correlationEntity;

    public Correlation() {
        this.correlationEntity = new CorrelationEntity();
    }

    public CorrelationEntity getCorrelationEntity(String identifier) {
        correlationEntity.setIdentifier(identifier);
        return correlationEntity;
    }
}
