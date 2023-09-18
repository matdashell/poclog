package com.poczinha.log.bean;

public class SessionIdentifier {
    private String identifier;

    public SessionIdentifier() {
        this.identifier = "anonymous";
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public boolean canLog(boolean ignoreOnEmptyHeader) {
        return !ignoreOnEmptyHeader || !identifier.equals("anonymous");
    }
}
