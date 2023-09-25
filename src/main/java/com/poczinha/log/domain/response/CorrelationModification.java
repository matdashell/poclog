package com.poczinha.log.domain.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.poczinha.log.domain.response.data.GroupTypeModifications;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@JsonPropertyOrder({"identifier", "date", "entities"})
public class CorrelationModification {
    private String identifier;
    private final LocalDateTime date;

    private final List<GroupTypeModifications> groups = new ArrayList<>();

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

    public List<GroupTypeModifications> getEntities() {
        return groups;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
}
