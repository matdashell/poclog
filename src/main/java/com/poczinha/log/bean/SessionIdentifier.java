package com.poczinha.log.bean;

import com.poczinha.log.domain.response.CorrelationModification;
import com.poczinha.log.domain.response.data.GroupTypeModifications;

import java.util.List;

public class SessionIdentifier {

    private final CorrelationModification correlationModification;

    public SessionIdentifier() {
        this.correlationModification = new CorrelationModification();
    }

    public String getIdentifier() {
        return correlationModification.getIdentifier();
    }

    public void setIdentifier(String identifier) {
        this.correlationModification.setIdentifier(identifier);
    }

    public boolean canLog(boolean ignoreOnEmptyHeader) {
        return !ignoreOnEmptyHeader || !correlationModification.getIdentifier().equals("anonymous");
    }

    public void addGroup(GroupTypeModifications group) {
        correlationModification.getEntities().add(group);
    }
}
