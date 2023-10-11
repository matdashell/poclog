package com.poczinha.log.hibernate.repository;

import com.poczinha.log.domain.response.CorrelationModification;
import com.poczinha.log.domain.response.PeriodModification;
import com.poczinha.log.domain.response.data.FieldModification;
import com.poczinha.log.domain.response.data.GroupTypeModification;
import com.poczinha.log.domain.response.data.TableModification;
import com.poczinha.log.hibernate.entity.LogRegisterEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RegisterRepository extends JpaRepository<LogRegisterEntity, Long> {

    @Query("SELECT DISTINCT new com.poczinha.log.domain.response.PeriodModification(" +
                " r.correlation.identifier," +
                " r.correlation.id," +
                " r.correlation.date" +
            " ) FROM LogRegisterEntity r" +
            " WHERE r.correlation.date BETWEEN :start AND :end" +
            " AND r.correlation.active = 1" +
            " ORDER BY r.correlation.date DESC")
    Page<PeriodModification> findAllByDateBetween(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            Pageable pageRequest);

    @Query("SELECT DISTINCT new com.poczinha.log.domain.response.CorrelationModification(" +
                " r.correlation.identifier," +
                " r.correlation.id," +
                " r.correlation.date" +
            " ) FROM LogRegisterEntity r" +
            " WHERE r.correlation.id = :correlation" +
            " AND r.correlation.active = 1")
    Optional<CorrelationModification> findAllCorrelationModification(@Param("correlation") Long correlation);

    @Query("SELECT DISTINCT new com.poczinha.log.domain.response.data.GroupTypeModification(" +
                " r.type" +
            " ) FROM LogRegisterEntity r" +
            " WHERE r.correlation.id = :correlation" +
            " AND r.correlation.active = 1" +
            " AND r.column.table = :table" +
            " ORDER BY r.type ASC")
    List<GroupTypeModification> findAllGroupTypesByCorrelationAndTable(
            @Param("correlation") Long correlation,
            @Param("table") String table);

    @Query("SELECT DISTINCT new com.poczinha.log.domain.response.data.FieldModification(" +
                " r.column.field," +
                " r.newValue," +
                " r.column.role" +
            " ) FROM LogRegisterEntity r" +
            " WHERE r.correlation.id = :correlation" +
            " AND r.type = :type" +
            " AND r.column.table = :tableName" +
            " AND r.column.active = 1" +
            " ORDER BY r.column.field ASC")
    List<FieldModification> findAllFieldModifications(
            @Param("correlation") Long correlation,
            @Param("tableName") String tableName,
            @Param("type") String type);

    @Query("SELECT r.newValue" +
            " FROM LogRegisterEntity r" +
            " WHERE r.type IN(:types)" +
            " AND r.column.table = :tableName" +
            " AND r.column.field = :columnName" +
            " AND r.correlation.id < :correlation" +
            " AND r.newValue <> :newValue" +
            " ORDER BY r.correlation.id DESC")
    Page<String> findFieldLastValueFromModification(
            @Param("types") List<String> types,
            @Param("tableName") String tableName,
            @Param("correlation") Long correlation,
            @Param("columnName") String columnName,
            @Param("newValue") String newValue,
            Pageable pageRequest);

    @Query("SELECT DISTINCT new com.poczinha.log.domain.response.data.TableModification(" +
                " r.column.table" +
            " ) FROM LogRegisterEntity r" +
            " WHERE r.correlation.id = :correlation" +
            " ORDER BY r.column.table ASC")
    List<TableModification> findAllTablesByCorrelation(@Param("correlation") Long correlationId);
}
