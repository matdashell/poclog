package com.poczinha.log.hibernate.entity;

import com.poczinha.log.domain.TypeEnum;

import javax.persistence.*;

@Entity
@Table(name = "tb_log_register")
public class RegisterEntity {

    @Id
    @Column(name = "id", columnDefinition = "bigint")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "identifier", columnDefinition = "varchar(30)")
    private String identifier;

    @Column(name = "previus_value", columnDefinition = "varchar(255)")
    private String lastValue;

    @Column(name = "new_value", columnDefinition = "varchar(255)")
    private String newValue;

    @Column(name = "type", columnDefinition = "enum('C','U','D')")
    @Enumerated(EnumType.STRING)
    private TypeEnum type;

    @ManyToOne(cascade = {CascadeType.MERGE})
    @JoinColumn(name = "correlation_id", referencedColumnName = "id", columnDefinition = "bigint")
    private CorrelationEntity correlation;

    @ManyToOne(cascade = {CascadeType.MERGE})
    @JoinColumn(name = "table_id", referencedColumnName = "id", columnDefinition = "int")
    private TableEntity table;

    @ManyToOne(cascade = {CascadeType.MERGE})
    @JoinColumn(name = "column_id", referencedColumnName = "id", columnDefinition = "int")
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
