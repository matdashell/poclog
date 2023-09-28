package com.poczinha.log.hibernate.repository;

import com.poczinha.log.domain.response.CorrelationModification;
import com.poczinha.log.domain.response.PeriodModification;
import com.poczinha.log.domain.response.data.FieldModification;
import com.poczinha.log.domain.response.data.GroupTypeModifications;
import com.poczinha.log.hibernate.entity.RegisterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RegisterRepository extends JpaRepository<RegisterEntity, Long> {

    @Query("SELECT DISTINCT new com.poczinha.log.domain.response.PeriodModification(" +
                " r.correlation.identifier," +
                " r.correlation.id," +
                " r.correlation.date" +
            " )" +
            " FROM RegisterEntity r" +
            " WHERE r.correlation.date BETWEEN :start AND :end" +
            " ORDER BY r.correlation.date DESC")
    List<PeriodModification> findAllByDateBetween(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    @Query("SELECT DISTINCT new com.poczinha.log.domain.response.CorrelationModification(" +
                " r.correlation.identifier," +
                " r.correlation.id," +
                " r.correlation.date" +
            " )" +
            " FROM RegisterEntity r" +
            " WHERE r.correlation.id = :correlation")
    CorrelationModification findAllCorrelationModification(@Param("correlation") Long correlation);

    @Query("SELECT DISTINCT new com.poczinha.log.domain.response.data.GroupTypeModifications(" +
                " r.type" +
            " )" +
            " FROM RegisterEntity r" +
            " WHERE r.correlation.id = :correlation" +
            " ORDER BY r.type ASC")
    List<GroupTypeModifications> findAllGroupTypesByCorrelation(@Param("correlation") Long correlation);

    @Query("SELECT DISTINCT new com.poczinha.log.domain.response.data.FieldModification(" +
                " r.column.name," +
                " r.lastValue," +
                " r.newValue" +
            " )" +
            " FROM RegisterEntity r" +
            " WHERE r.correlation.id = :correlation" +
            " AND r.type = :type" +
            " ORDER BY r.column.name ASC")
    List<FieldModification> findAllFieldModifications(
            @Param("correlation") Long correlation,
            @Param("type") String type);
}
