package com.poczinha.log.hibernate.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_historico_correlacionador")
public class LogCorrelationEntity {

    @Id
    @Column(name = "CdLogCorrelacionador", columnDefinition = "bigint")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "DsIdentificador", columnDefinition = "varchar(30)", nullable = false)
    private String identifier;

    @Column(name = "DtInclusao", columnDefinition = "datetime", nullable = false)
    private LocalDateTime date;

    @Column(name = "FlAtivo", columnDefinition = "boolean", nullable = false)
    private boolean active;

    public LogCorrelationEntity() {
        this.date = LocalDateTime.now();
        this.active = true;
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

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
