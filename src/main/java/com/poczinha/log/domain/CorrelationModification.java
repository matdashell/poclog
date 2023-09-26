package com.poczinha.log.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.poczinha.log.domain.data.GroupTypeModifications;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"identifier", "date", "entities"})
public class CorrelationModification {
    private String identifier;
    private String audit;
    private final LocalDateTime date;

    private List<GroupTypeModifications> groups = new ArrayList<>();

    public CorrelationModification() {
        this.identifier = "anonymous";
        this.date = LocalDateTime.now();
    }

    public String getIdentifier() {
        return identifier;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public List<GroupTypeModifications> getGroups() {
        return groups;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getAudit() {
        return audit;
    }

    public void setAudit(String audit) {
        this.audit = audit;
    }

    public void setGroups(List<GroupTypeModifications> groups) {
        this.groups = groups;
    }
}
