package com.poczinha.log.hibernate.service;

import com.poczinha.log.hibernate.entity.TableEntity;
import com.poczinha.log.hibernate.repository.TableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TableService {

    @Autowired
    private TableRepository tableRepository;

    public TableEntity tableEntityWithName(String name) {
        TableEntity byName = tableRepository.findByName(name);
        if (byName == null) {
            byName = new TableEntity(name);
            return tableRepository.save(byName);
        }
        return byName;
    }
}
