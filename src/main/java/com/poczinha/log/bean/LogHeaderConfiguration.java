package com.poczinha.log.bean;

public class LogHeaderConfiguration {

    private String id = "notDefined";
    private final boolean ignoreIfNotExists;

    public LogHeaderConfiguration(boolean ignoreIfNotExists) {
        this.ignoreIfNotExists = ignoreIfNotExists;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public boolean canLog() {
        return !ignoreIfNotExists || !id.equals("notDefined");
    }
}
