package com.poczinha.log.helper.ok.repository;

import com.poczinha.log.helper.ok.entity.BearEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BearRepository extends JpaRepository<BearEntity, Integer> {
}
