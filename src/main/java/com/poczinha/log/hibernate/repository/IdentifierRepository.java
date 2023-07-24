package com.poczinha.log.hibernate.repository;

import com.poczinha.log.hibernate.entity.IdentifierEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface IdentifierRepository extends JpaRepository<IdentifierEntity, Integer> {

    @Query(value = "SELECT id FROM IdentifierEntity WHERE name = :identifierName")
    Integer findIdByName(@Param("identifierName") String identifierName);
}
