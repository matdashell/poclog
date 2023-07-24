package com.poczinha.log.hibernate.entity;

import com.poczinha.log.hibernate.entity.base.BaseEntity;

import jakarta.persistence.*;

@Entity
public class RegisterEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String type;

    private String lastValue;

    private String newValue;

    @ManyToOne(targetEntity = TableEntity.class, fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "table_id", referencedColumnName = "id")
    private TableEntity table;

    @ManyToOne(targetEntity = ColumnEntity.class, fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinColumn(name = "column_id", referencedColumnName = "id")
    private ColumnEntity column;

    @ManyToOne(targetEntity = IdentifierEntity.class, fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "identifier_id", referencedColumnName = "id")
    private IdentifierEntity identifier;

    public RegisterEntity(String type, TableEntity table, ColumnEntity column, IdentifierEntity identifier, String lastValue, String newValue) {
        this.type = type;
        this.table = table;
        this.column = column;
        this.identifier = identifier;
        this.lastValue = lastValue;
        this.newValue = newValue;
        super.register();
    }

    public RegisterEntity() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public TableEntity getTable() {
        return table;
    }

    public void setTable(TableEntity table) {
        this.table = table;
    }

    public ColumnEntity getColumn() {
        return column;
    }

    public void setColumn(ColumnEntity column) {
        this.column = column;
    }

    public IdentifierEntity getIdentifier() {
        return identifier;
    }

    public void setIdentifier(IdentifierEntity identifier) {
        this.identifier = identifier;
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
}
