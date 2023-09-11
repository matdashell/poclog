package com.poczinha.log.hibernate.domain.response.data;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.poczinha.log.hibernate.domain.TypeEnum;

import java.util.ArrayList;
import java.util.List;

@JsonPropertyOrder({"entity", "type", "modifications"})
public class EntityModification {
    private final String entity;
    private final TypeEnum type;

    private final List<FieldModification> modifications = new ArrayList<>();

    public EntityModification(String entity, TypeEnum type) {
        this.entity = entity;
        this.type = type;
    }

    public String getEntity() {
        return entity;
    }

    public TypeEnum getType() {
        return type;
    }

    public List<FieldModification> getModifications() {
        return modifications;
    }
}
