package com.poczinha.log.hibernate.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_log_table")
public class TableEntity {

    @Id
    @Column(name = "id", columnDefinition = "int")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", columnDefinition = "varchar(60)")
    private String name;

    @Column(name = "includedAt", columnDefinition = "datetime")
    private LocalDateTime includedAt;

    public TableEntity(String name) {
        this.name = name;
        this.includedAt = LocalDateTime.now();
    }

    public TableEntity() {
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

    public LocalDateTime getIncludedAt() {
        return includedAt;
    }

    public void setIncludedAt(LocalDateTime includedAt) {
        this.includedAt = includedAt;
    }
}
