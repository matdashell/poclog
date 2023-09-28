package com.poczinha.log.hibernate.repository;

import com.poczinha.log.hibernate.entity.ColumnEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ColumnRepository extends JpaRepository<ColumnEntity, Integer> {
    @Query("SELECT new ColumnEntity(c.id) FROM ColumnEntity c WHERE c.name = :name")
    ColumnEntity findIdByName(@Param("name") String name);
}
