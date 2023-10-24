package com.poczinha.log.service;

import com.poczinha.log.hibernate.entity.LogTableEntity;
import com.poczinha.log.hibernate.repository.TableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TableService {

    @Autowired
    private TableRepository tableRepository;

    public LogTableEntity retrieveOrStore(String tableName) {
        LogTableEntity tableEntity = findByName(tableName);
        return tableEntity != null ? tableEntity : createTable(tableName);
    }

    public LogTableEntity createTable(String tableName) {
        LogTableEntity tableEntity = new LogTableEntity(tableName);
        return tableRepository.save(tableEntity);
    }

    public LogTableEntity findByName(String tableName) {
        return tableRepository.findByTableName(tableName);
    }
}
