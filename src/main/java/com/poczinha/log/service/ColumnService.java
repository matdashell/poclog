package com.poczinha.log.service;

import com.poczinha.log.hibernate.entity.LogColumnEntity;
import com.poczinha.log.hibernate.repository.ColumnRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ColumnService {

    @Autowired
    private ColumnRepository columnRepository;

    public LogColumnEntity retrieveOrStoreByTableAndColumn(String tableName, String fieldName) {
        LogColumnEntity columnEntity = findByNameAndTable(tableName, fieldName);
        return columnEntity != null ? columnEntity : createColumn(tableName, fieldName);
    }

    public LogColumnEntity findByNameAndTable(String tableName, String fieldName) {
        return columnRepository.findIdByTableAndField(tableName, fieldName);
    }

    public LogColumnEntity createColumn(String tableName, String fieldName) {
        LogColumnEntity columnEntity = new LogColumnEntity(tableName, fieldName);
        LogColumnEntity save = columnRepository.save(columnEntity);
        return new LogColumnEntity(
                save.getId(),
                save.isActive()
        );
    }
}
