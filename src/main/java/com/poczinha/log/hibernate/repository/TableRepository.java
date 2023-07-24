package com.poczinha.log.hibernate.repository;

import com.poczinha.log.hibernate.entity.TableEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TableRepository extends JpaRepository<TableEntity, Integer> {

    @Query(value = "SELECT id FROM TableEntity WHERE name = :tableName")
    Integer findIdByName(@Param("tableName") String tableName);
}
