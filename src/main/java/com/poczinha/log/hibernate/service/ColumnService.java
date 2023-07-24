package com.poczinha.log.hibernate.service;

import com.poczinha.log.hibernate.entity.ColumnEntity;
import com.poczinha.log.hibernate.entity.TableEntity;
import com.poczinha.log.hibernate.repository.ColumnRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ColumnService {

    @Autowired
    private ColumnRepository columnRepository;

    public Integer getColumnIdInTable(String columnName, Integer tableId) {
        return columnRepository.findIdByNameAndTableId(columnName, tableId);
    }

    public Integer createColumn(String columnName, Integer tableId) {
        TableEntity tableEntity = new TableEntity(tableId);
        ColumnEntity columnEntity = new ColumnEntity(columnName, tableEntity);
        return columnRepository.save(columnEntity).getId();
    }
}
