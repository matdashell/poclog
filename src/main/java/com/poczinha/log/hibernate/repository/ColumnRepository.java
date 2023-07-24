package com.poczinha.log.hibernate.repository;

import com.poczinha.log.hibernate.entity.ColumnEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ColumnRepository extends JpaRepository<ColumnEntity, Integer> {

    @Query(value = "SELECT id FROM ColumnEntity WHERE name = :columnName AND table.id = :tableId")
    Integer findIdByNameAndTableId(@Param("columnName") String columnName, @Param("tableId") Integer tableId);
}
