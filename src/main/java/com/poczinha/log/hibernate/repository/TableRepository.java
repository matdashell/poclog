package com.poczinha.log.hibernate.repository;

import com.poczinha.log.hibernate.entity.TableEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TableRepository extends JpaRepository<TableEntity, Integer> {
    TableEntity findByName(String entity);
}
