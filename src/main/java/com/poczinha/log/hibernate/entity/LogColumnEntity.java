package com.poczinha.log.hibernate.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_historico_colunas")
public class LogColumnEntity {

    @Id
    @Column(name = "CdLogCampo", columnDefinition = "bigint")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = {CascadeType.MERGE})
    @JoinColumn(name = "CdLogTabela", referencedColumnName = "CdLogTabela", columnDefinition = "bigint", nullable = false)
    private LogTableEntity table;

    @Column(name = "DsCampo", columnDefinition = "varchar(60)", nullable = false)
    private String field;

    @Column(name = "DtInclusao", columnDefinition = "datetime", nullable = false)
    private LocalDateTime includedAt;

    @Column(name = "DtAlteracao", columnDefinition = "datetime")
    private LocalDateTime alterAt;

    @Column(name = "FlAtivo", columnDefinition = "boolean", nullable = false)
    private boolean active;

    @Column(name = "DsPermissao", columnDefinition = "char(12)")
    private String role;

    public LogColumnEntity(LogTableEntity table, String fieldName) {
        this.field = fieldName;
        this.active = true;
        this.table = table;
        this.includedAt = LocalDateTime.now();
    }

    public LogColumnEntity(Long id, Boolean active) {
        this.id = id;
        this.active = active;
    }

    public LogColumnEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public LocalDateTime getIncludedAt() {
        return includedAt;
    }

    public void setIncludedAt(LocalDateTime includedAt) {
        this.includedAt = includedAt;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public LocalDateTime getAlterAt() {
        return alterAt;
    }

    public void setAlterAt(LocalDateTime alteredAt) {
        this.alterAt = alteredAt;
    }

    public LogTableEntity getTable() {
        return table;
    }

    public void setTable(LogTableEntity table) {
        this.table = table;
    }
}
