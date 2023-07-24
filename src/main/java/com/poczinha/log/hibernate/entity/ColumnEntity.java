package com.poczinha.log.hibernate.entity;

import com.poczinha.log.hibernate.entity.base.BaseEntity;
import jakarta.persistence.*;

@Entity
public class ColumnEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    @JoinColumn(name = "table_id", referencedColumnName = "id")
    @ManyToOne(targetEntity = TableEntity.class, fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    private TableEntity table;

    public ColumnEntity(Integer id) {
        this.id = id;
    }

    public ColumnEntity(String columnName, TableEntity table) {
        this.name = columnName;
        this.table = table;
        super.register();
    }

    public ColumnEntity() {
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

    public TableEntity getTable() {
        return table;
    }

    public void setTable(TableEntity table) {
        this.table = table;
    }
}
