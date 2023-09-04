package com.poczinha.log.hibernate.domain.response;

import com.poczinha.log.hibernate.domain.TypeEnum;

import java.time.LocalDateTime;

public class TypeDate {
    private final TypeEnum type;
    private final String entity;
    private final LocalDateTime date;

    public TypeDate(TypeEnum type, String entity, LocalDateTime date) {
        this.type = type;
        this.entity = entity;
        this.date = date;
    }

    public TypeEnum getType() {
        return type;
    }

    public String getEntity() {
        return entity;
    }

    public LocalDateTime getDate() {
        return date;
    }
}
