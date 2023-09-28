package com.poczinha.log.bean;

import com.poczinha.log.hibernate.entity.CorrelationEntity;

public class Correlation {
    private final CorrelationEntity correlationEntity;

    public Correlation(String identifier) {
        this.correlationEntity = new CorrelationEntity();
        this.correlationEntity.setIdentifier(identifier);
    }

    public CorrelationEntity getCorrelationEntity() {
        return correlationEntity;
    }

    public void setIdentifier(String identifier) {
        this.correlationEntity.setIdentifier(identifier);
    }

    public boolean canLog(boolean ignoreOnEmptyHeader) {
        return !ignoreOnEmptyHeader || !correlationEntity.getIdentifier().equals("anonymous");
    }
}
