package com.poczinha.log.hibernate.repository;

import com.poczinha.log.hibernate.entity.LogTableEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TableRepository extends JpaRepository<LogTableEntity, Long> {
    LogTableEntity findByTableName(String tableName);
}
