package com.poczinha.log.hibernate.repository;

import com.poczinha.log.hibernate.entity.LogCorrelationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CorrelationRepository extends JpaRepository<LogCorrelationEntity, Long> {
}
