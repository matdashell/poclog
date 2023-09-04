package com.poczinha.log.hibernate.entity;

import com.poczinha.log.hibernate.domain.TypeEnum;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "registerEntity")
public class RegisterEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    private TypeEnum type;
    private String entity;
    private String field;
    private String identifier;
    private LocalDateTime date;
    private String lastValue;
    private String newValue;
    private String correlation;

    public RegisterEntity(String entity, String field, String identifier, TypeEnum type, String lastValue, String newValue, LocalDateTime now, String correlation) {
        this.entity = entity;
        this.field = field;
        this.identifier = identifier;
        this.type = type;
        this.lastValue = lastValue;
        this.newValue = newValue;
        this.date = now;
        this.correlation = correlation;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public TypeEnum getType() {
        return type;
    }

    public void setType(TypeEnum type) {
        this.type = type;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getLastValue() {
        return lastValue;
    }

    public void setLastValue(String lastValue) {
        this.lastValue = lastValue;
    }

    public String getNewValue() {
        return newValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    public String getCorrelation() {
        return correlation;
    }

    public void setCorrelation(String correlation) {
        this.correlation = correlation;
    }
}
