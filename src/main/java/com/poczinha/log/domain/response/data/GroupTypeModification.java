package com.poczinha.log.domain.response.data;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.ArrayList;
import java.util.List;

@JsonPropertyOrder({"type", "modifications"})
public class GroupTypeModification {
    private final String type;

    private final List<FieldModification> modifications = new ArrayList<>();

    public GroupTypeModification(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public List<FieldModification> getModifications() {
        return modifications;
    }
}
