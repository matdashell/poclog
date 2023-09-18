package com.poczinha.log.bean;

public class TypeCountManager {
    private int creation;
    private int modification;
    private int deletion;

    private int persistSessionCreation;
    private int persistSessionModification;
    private int persistSessionDeletion;

    public TypeCountManager() {
        this.creation = -1;
        this.modification = -1;
        this.deletion = -1;

        this.persistSessionCreation = -1;
        this.persistSessionModification = -1;
        this.persistSessionDeletion = -1;
    }

    public String getCreation() {
        persistSessionCreation = creation;
        return "C" + creation;
    }

    public String getModification() {
        persistSessionModification = modification;
        return "U" + modification;
    }

    public String getDeletion() {
        persistSessionDeletion = deletion;
        return "D" + deletion;
    }

    public void countCreation() {
        if (this.creation == this.persistSessionCreation) {
            this.creation++;
        }
    }

    public void countModification() {
        if (this.modification == this.persistSessionModification) {
            this.modification++;
        }
    }

    public void countDeletion() {
        if (this.deletion == this.persistSessionDeletion) {
            this.deletion++;
        }
    }
}
