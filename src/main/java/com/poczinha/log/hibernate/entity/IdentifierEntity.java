package com.poczinha.log.hibernate.entity;

import com.poczinha.log.hibernate.entity.base.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class IdentifierEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    public IdentifierEntity(Integer id) {
        this.id = id;
    }

    public IdentifierEntity(String identifierName) {
        this.name = identifierName;
        super.register();
    }

    public IdentifierEntity() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
