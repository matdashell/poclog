package com.poczinha.log.hibernate.service;

import com.poczinha.log.hibernate.entity.IdentifierEntity;
import com.poczinha.log.hibernate.repository.IdentifierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IdentifierService {

    @Autowired
    private IdentifierRepository identifierRepository;

    public Integer getIdentifierId(String identifierName) {
        return identifierRepository.findIdByName(identifierName);
    }

    public Integer createIdentifier(String identifierName) {
        IdentifierEntity identifierEntity = new IdentifierEntity(identifierName);
        return identifierRepository.save(identifierEntity).getId();
    }
}
