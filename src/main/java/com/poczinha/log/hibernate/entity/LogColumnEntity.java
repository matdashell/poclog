package com.poczinha.log.hibernate.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_log_column")
public class LogColumnEntity {

    @Id
    @Column(name = "id", columnDefinition = "bigint")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "table_name", columnDefinition = "varchar(60)", nullable = false)
    private String table;

    @Column(name = "field_name", columnDefinition = "varchar(60)", nullable = false)
    private String field;

    @Column(name = "included_at", columnDefinition = "datetime", nullable = false)
    private LocalDateTime includedAt;

    @Column(name = "active_log", columnDefinition = "boolean", nullable = false)
    private boolean active;

    @Column(name = "view_role", columnDefinition = "char(12)")
    private String role;

    public LogColumnEntity(String tableName, String fieldName) {
        this.field = fieldName;
        this.active = true;
        this.table = tableName;
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

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
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
}
