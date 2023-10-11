package com.poczinha.log.hibernate.entity;

import javax.persistence.*;

@Entity
@Table(name = "tb_log_register")
public class LogRegisterEntity {

    @Id
    @Column(name = "CdLogRegistro", columnDefinition = "bigint")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "DsNovoValor", columnDefinition = "varchar(255)")
    private String newValue;

    @Column(name = "DsInfoAlteracao", columnDefinition = "char(20)", nullable = false)
    private String type;

    @ManyToOne(cascade = {CascadeType.MERGE})
    @JoinColumn(name = "CdLogCorrelacionador", referencedColumnName = "CdLogCorrelacionador", columnDefinition = "bigint", nullable = false)
    private LogCorrelationEntity correlation;

    @ManyToOne(cascade = {CascadeType.MERGE})
    @JoinColumn(name = "CdLogCampo", referencedColumnName = "CdLogCampo", columnDefinition = "bigint", nullable = false)
    private LogColumnEntity column;

    public LogRegisterEntity() {
    }

    public LogRegisterEntity(LogColumnEntity column, String newValue, String type) {
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

    public LogCorrelationEntity getCorrelation() {
        return correlation;
    }

    public void setCorrelation(LogCorrelationEntity correlation) {
        this.correlation = correlation;
    }

    public LogColumnEntity getColumn() {
        return column;
    }

    public void setColumn(LogColumnEntity column) {
        this.column = column;
    }
}
