package com.poczinha.log.hibernate.repository;

import com.poczinha.log.hibernate.entity.ColumnEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ColumnRepository extends JpaRepository<ColumnEntity, Integer> {
    ColumnEntity findByName(String name);
}
