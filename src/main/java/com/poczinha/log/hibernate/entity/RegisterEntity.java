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

    @Column(name = "previus_value", columnDefinition = "varchar(255)")
    private String lastValue;

    @Column(name = "new_value", columnDefinition = "varchar(255)")
    private String newValue;

    @Column(name = "type", columnDefinition = "char(4)")
    private String type;

    @ManyToOne(cascade = {CascadeType.MERGE})
    @JoinColumn(name = "correlation_id", referencedColumnName = "id", columnDefinition = "bigint")
    private CorrelationEntity correlation;

    @ManyToOne(cascade = {CascadeType.MERGE})
    @JoinColumn(name = "column_id", referencedColumnName = "id", columnDefinition = "int")
    private ColumnEntity column;

    public RegisterEntity() {
    }

    public RegisterEntity(CorrelationEntity correlation, ColumnEntity column, String lastValue, String newValue, String type) {
        this.correlation = correlation;
        this.column = column;
        this.lastValue = lastValue;
        this.newValue = newValue;
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public CorrelationEntity getCorrelation() {
        return correlation;
    }

    public void setCorrelation(CorrelationEntity correlation) {
        this.correlation = correlation;
    }

    public ColumnEntity getColumn() {
        return column;
    }

    public void setColumn(ColumnEntity column) {
        this.column = column;
    }
}
