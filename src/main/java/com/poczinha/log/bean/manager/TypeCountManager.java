package com.poczinha.log.bean.manager;

public class TypeCountManager {
    private int creation;
    private int modification;
    private int deletion;

    public TypeCountManager() {
        this.creation = 0;
        this.modification = 0;
        this.deletion = 0;
    }

    public String getCreation() {
        return "C" + creation;
    }

    public String getModification() {
        return "U" + modification;
    }

    public String getDeletion() {
        return "D" + deletion;
    }

    public void countCreation() {
        this.creation++;
    }

    public void countModification() {
        this.modification++;
    }

    public void countDeletion() {
        this.deletion++;
    }
}
