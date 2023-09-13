package com.poczinha.log.hibernate.domain;

import com.poczinha.log.hibernate.entity.CorrelationEntity;

public class Correlation {
    private final CorrelationEntity correlationEntity;

    public Correlation() {
        this.correlationEntity = new CorrelationEntity();
    }

    public CorrelationEntity getCorrelationEntity() {
        return correlationEntity;
    }
}
