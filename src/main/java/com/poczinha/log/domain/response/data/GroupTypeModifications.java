package com.poczinha.log.domain.response.data;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.poczinha.log.domain.TypeEnum;

import java.util.ArrayList;
import java.util.List;

@JsonPropertyOrder({"type", "modifications"})
public class GroupTypeModifications {
    private final String type;

    private final List<FieldModification> modifications = new ArrayList<>();

    public GroupTypeModifications(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public List<FieldModification> getModifications() {
        return modifications;
    }
}
