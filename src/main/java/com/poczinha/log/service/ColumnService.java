package com.poczinha.log.service;

import com.poczinha.log.hibernate.entity.ColumnEntity;
import com.poczinha.log.hibernate.repository.ColumnRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ColumnService {

    @Autowired
    private ColumnRepository columnRepository;

    public ColumnEntity columnEntityWithName(String tableName, String fieldName) {
        ColumnEntity columnEntity = columnRepository.findIdByTableAndField(tableName, fieldName);
        if (columnEntity == null) {
            columnEntity = new ColumnEntity(tableName, fieldName);
            ColumnEntity save = columnRepository.save(columnEntity);
            return new ColumnEntity(
                    save.getId(),
                    save.isActive()
            );
        }
        return columnEntity;
    }
}
