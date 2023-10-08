package com.poczinha.log.hibernate.entity;

import javax.persistence.*;

@Entity
@Table(name = "tb_log_register")
public class RegisterEntity {

    @Id
    @Column(name = "id", columnDefinition = "bigint")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "new_value", columnDefinition = "varchar(255)")
    private String newValue;

    @Column(name = "type", columnDefinition = "char(20)", nullable = false)
    private String type;

    @ManyToOne(cascade = {CascadeType.MERGE})
    @JoinColumn(name = "correlation_id", referencedColumnName = "id", columnDefinition = "bigint", nullable = false)
    private CorrelationEntity correlation;

    @ManyToOne(cascade = {CascadeType.MERGE})
    @JoinColumn(name = "column_id", referencedColumnName = "id", columnDefinition = "int", nullable = false)
    private ColumnEntity column;

    public RegisterEntity() {
    }

    public RegisterEntity(ColumnEntity column, String newValue, String type) {
        this.column = column;
        this.newValue = newValue;
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
