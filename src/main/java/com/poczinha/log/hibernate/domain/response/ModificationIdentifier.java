package com.poczinha.log.hibernate.domain.response;

import com.poczinha.log.hibernate.domain.TypeEnum;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ModificationIdentifier {
    private final TypeEnum type;
    private final String entity;
    private final LocalDateTime date;
    private List<ModificationEntity> modifications = new ArrayList<>();

    public TypeEnum getType() {
        return type;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public List<ModificationEntity> getModifications() {
        return modifications;
    }

    public String getEntity() {
        return entity;
    }

    public void setModifications(List<ModificationEntity> modifications) {
        this.modifications = modifications;
    }

    public ModificationIdentifier(TypeEnum type, String entity, LocalDateTime date) {
        this.type = type;
        this.entity = entity;
        this.date = date;
    }
}
