package com.poczinha.log.hibernate.repository;

import com.poczinha.log.hibernate.entity.RegisterEntity;
import com.poczinha.log.hibernate.entity.TableEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RegisterRepository extends JpaRepository<RegisterEntity, Integer> {

    @Query("SELECT DISTINCT r.identifier.name FROM RegisterEntity r WHERE r.createdAt BETWEEN :start AND :end")
    List<String> getAllIdentifierNamesByCreatedAtBetween(
            @Param("start") LocalDateTime startDate,
            @Param("end") LocalDateTime endDate
    );

    @Query("SELECT DISTINCT r.table FROM RegisterEntity r WHERE r.identifier.name = :identifierName AND r.createdAt BETWEEN :start AND :end")
    List<TableEntity> findAllTableByIdentifierNameAndCreatedAtBetween(
            @Param("identifierName") String identifierName,
            @Param("start") LocalDateTime startDate,
            @Param("end") LocalDateTime endDate
    );

    List<RegisterEntity> findAllByTableAndIdentifierNameAndCreatedAtBetweenOrderByCreatedAt(
            TableEntity table,
            String identifierName,
            LocalDateTime startDate,
            LocalDateTime endDate
    );
}
