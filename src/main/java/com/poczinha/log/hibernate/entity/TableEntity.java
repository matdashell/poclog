package com.poczinha.log.hibernate.entity;

import com.poczinha.log.hibernate.entity.base.BaseEntity;

import jakarta.persistence.*;

@Entity
public class TableEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    public TableEntity(Integer id) {
        this.id = id;
    }

    public TableEntity(String name) {
        this.name = name;
        super.register();
    }

    public TableEntity() {
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
