package com.poczinha.log.domain.response.data;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.ArrayList;
import java.util.List;

@JsonPropertyOrder({"type", "modifications"})
public class GroupTypeModifications {
    private String type;

    private final List<FieldModification> modifications = new ArrayList<>();

    public GroupTypeModifications() {
    }

    public String getType() {
        return type;
    }

    public List<FieldModification> getModifications() {
        return modifications;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void addModifications(List<FieldModification> modifications) {
        this.modifications.addAll(modifications);
    }
}
