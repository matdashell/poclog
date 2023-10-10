package com.poczinha.log.hibernate.repository;

import com.poczinha.log.hibernate.entity.LogColumnEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ColumnRepository extends JpaRepository<LogColumnEntity, Long> {
    @Query("SELECT new ColumnEntity(c.id, c.active) FROM ColumnEntity c WHERE c.table = :table AND c.field = :field")
    LogColumnEntity findIdByTableAndField(
            @Param("table") String tableName,
            @Param("field") String field);
}
