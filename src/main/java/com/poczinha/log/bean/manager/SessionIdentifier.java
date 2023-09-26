package com.poczinha.log.bean.manager;

import com.poczinha.log.domain.CorrelationModification;
import com.poczinha.log.domain.data.GroupTypeModifications;

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
        correlationModification.getGroups().add(group);
    }

    public boolean containsData() {
        return !correlationModification.getGroups().isEmpty();
    }

    public CorrelationModification getCorrelationModification() {
        return correlationModification;
    }
}
