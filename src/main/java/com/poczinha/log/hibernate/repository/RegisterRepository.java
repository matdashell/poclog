package com.poczinha.log.hibernate.repository;

import com.poczinha.log.hibernate.domain.TypeEnum;
import com.poczinha.log.hibernate.domain.response.PeriodModification;
import com.poczinha.log.hibernate.domain.response.data.EntityModification;
import com.poczinha.log.hibernate.domain.response.data.FieldModification;
import com.poczinha.log.hibernate.entity.RegisterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RegisterRepository extends JpaRepository<RegisterEntity, Integer> {

    @Query("SELECT DISTINCT new com.poczinha.log.hibernate.domain.response.PeriodModification(r.identifier, r.correlation, r.date) " +
            "FROM RegisterEntity r " +
            "WHERE r.date BETWEEN :start AND :end " +
            "ORDER BY r.date DESC")
    List<PeriodModification> getAllPeriodModificationBetween(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    @Query("SELECT DISTINCT r.identifier " +
            "FROM RegisterEntity r " +
            "WHERE r.correlation = :correlation")
    String getIdentifierByCorrelation(@Param("correlation") String correlation);

    @Query("SELECT DISTINCT new com.poczinha.log.hibernate.domain.response.data.EntityModification(r.entity, r.type) " +
            "FROM RegisterEntity r " +
            "WHERE r.correlation = :correlation")
    List<EntityModification> getAllModificationsByCorrelation(@Param("correlation") String correlation);

    @Query("SELECT DISTINCT new com.poczinha.log.hibernate.domain.response.data.FieldModification(r.field, r.lastValue, r.newValue) " +
            "FROM RegisterEntity r " +
            "WHERE r.correlation = :correlation " +
            "AND r.entity = :entity " +
            "AND r.type = :type")
    List<FieldModification> getAllModificationsByCorrelationAndEntityAndType(
            @Param("correlation") String correlation,
            @Param("entity") String entity,
            @Param("type") TypeEnum type);
}
