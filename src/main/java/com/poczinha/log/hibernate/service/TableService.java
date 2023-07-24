package com.poczinha.log.hibernate.service;

import com.poczinha.log.hibernate.entity.TableEntity;
import com.poczinha.log.hibernate.repository.TableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TableService {

    @Autowired
    private TableRepository tableRepository;

    public Integer getTableId(String tableName) {
        return tableRepository.findIdByName(tableName);
    }

    public Integer createTable(String tableName) {
        TableEntity tableEntity = new TableEntity(tableName);
        return tableRepository.save(tableEntity).getId();
    }
}
