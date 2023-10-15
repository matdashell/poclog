package com.poczinha.log.helper.ok.entity;

import com.poczinha.log.annotation.LogEntity;
import com.poczinha.log.annotation.LogField;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@LogEntity(name = "Sr Bear")
public class BearEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Integer id;

    @LogField(name = "Name of Sr Bear")
    private String name;

    @LogField(name = "Age of Sr Bear")
    private int age;

    @LogField(name = "Food of Sr Bear")
    private PersonEntity food;

    public BearEntity() {
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

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public PersonEntity getFood() {
        return food;
    }

    public void setFood(PersonEntity food) {
        this.food = food;
    }
}
