package com.poczinha.log.hibernate.repository;

import com.poczinha.log.hibernate.domain.TypeEnum;
import com.poczinha.log.hibernate.domain.response.IdentifierDate;
import com.poczinha.log.hibernate.domain.response.ModificationEntity;
import com.poczinha.log.hibernate.domain.response.TypeDate;
import com.poczinha.log.hibernate.entity.RegisterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RegisterRepository extends JpaRepository<RegisterEntity, Integer> {

    @Query("SELECT DISTINCT new com.poczinha.log.hibernate.domain.response.IdentifierDate(r.identifier, r.entity, r.date) FROM RegisterEntity r WHERE r.date BETWEEN :start AND :end")
    List<IdentifierDate> findAllByDateBetween(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    @Query("SELECT DISTINCT new com.poczinha.log.hibernate.domain.response.TypeDate(r.type, r.entity, r.date) FROM RegisterEntity r WHERE r.identifier = :identifier AND r.date BETWEEN :start AND :end")
    List<TypeDate> findAllDistinctsByIdentifierAndDateBetween(
            @Param("identifier") String identifier,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    List<ModificationEntity> findAllByTypeAndDateAndIdentifier(TypeEnum type, LocalDateTime date, String identifier);
}
