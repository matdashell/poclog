package com.poczinha.log.hibernate.service;

import com.poczinha.log.hibernate.entity.CorrelationEntity;
import com.poczinha.log.hibernate.repository.CorrelationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CorrelationService {

    @Autowired
    private CorrelationRepository correlationRepository;

    public void save(CorrelationEntity correlation) {
        if (correlation.getId() == null) {
            correlationRepository.save(correlation);
        }
    }
}
