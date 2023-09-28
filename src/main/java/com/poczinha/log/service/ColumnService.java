package com.poczinha.log.service;

import com.poczinha.log.hibernate.entity.ColumnEntity;
import com.poczinha.log.hibernate.repository.ColumnRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ColumnService {

    @Autowired
    private ColumnRepository columnRepository;

    public ColumnEntity columnEntityWithName(String name) {
        ColumnEntity byName = columnRepository.findIdByName(name);
        if (byName == null) {
            byName = new ColumnEntity(name);
            return columnRepository.save(byName);
        }
        return byName;
    }
}
