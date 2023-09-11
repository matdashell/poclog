package com.poczinha.log.hibernate.entity;

import com.poczinha.log.hibernate.domain.TypeEnum;

import javax.persistence.*;

@Entity
@Table(name = "tb_log_register")
public class RegisterEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String identifier;
    private String lastValue;
    private String newValue;

    @Enumerated(EnumType.STRING)
    private TypeEnum type;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "correlation_id", referencedColumnName = "id")
    private CorrelationEntity correlation;

    @ManyToOne(cascade = {CascadeType.MERGE})
    @JoinColumn(name = "table_id", referencedColumnName = "id")
    private TableEntity table;

    @ManyToOne(cascade = {CascadeType.MERGE})
    @JoinColumn(name = "column_id", referencedColumnName = "id")
    private ColumnEntity column;

    public RegisterEntity(CorrelationEntity correlation, ColumnEntity column, TableEntity table, String identifier, String lastValue, String newValue, TypeEnum type) {
        this.correlation = correlation;
        this.column = column;
        this.table = table;
        this.identifier = identifier;
        this.lastValue = lastValue;
        this.newValue = newValue;
        this.type = type;
    }

    public RegisterEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
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

    public TypeEnum getType() {
        return type;
    }

    public void setType(TypeEnum type) {
        this.type = type;
    }

    public CorrelationEntity getCorrelation() {
        return correlation;
    }

    public void setCorrelation(CorrelationEntity correlation) {
        this.correlation = correlation;
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
}
