package com.poczinha.log.hibernate.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_historico_tabelas")
public class LogTableEntity {

    @Id
    @Column(name = "CdLogTabela", columnDefinition = "bigint")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "DsTabela", columnDefinition = "varchar(60)", nullable = false)
    private String tableName;

    @Column(name = "DtInclusao", columnDefinition = "datetime", nullable = false)
    private LocalDateTime includedAt;

    @Column(name = "DtAlteracao", columnDefinition = "datetime")
    private LocalDateTime alterAt;

    @Column(name = "FlAtivo", columnDefinition = "boolean", nullable = false)
    private boolean active;

    public LogTableEntity(String table) {
        this.tableName = table;
        this.active = true;
        this.includedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public LocalDateTime getIncludedAt() {
        return includedAt;
    }

    public void setIncludedAt(LocalDateTime includedAt) {
        this.includedAt = includedAt;
    }

    public LocalDateTime getAlterAt() {
        return alterAt;
    }

    public void setAlterAt(LocalDateTime alterAt) {
        this.alterAt = alterAt;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
