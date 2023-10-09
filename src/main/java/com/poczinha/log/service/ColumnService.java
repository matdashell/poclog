package com.poczinha.log.service;

import com.poczinha.log.bean.LogColumnCache;
import com.poczinha.log.hibernate.entity.ColumnEntity;
import com.poczinha.log.hibernate.repository.ColumnRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

@Service
public class ColumnService {

    @Autowired
    private ColumnRepository columnRepository;

    @Autowired
    private LogColumnCache logColumnCache;

    public ColumnEntity retrieveOrStoreByTableAndColumn(String tableName, String fieldName) {
        ColumnEntity columnEntity = findByNameAndTable(tableName, fieldName);
        return columnEntity != null ? columnEntity : createColumn(tableName, fieldName);
    }

    public ColumnEntity findByNameAndTable(String tableName, String fieldName) {
        return columnRepository.findIdByTableAndField(tableName, fieldName);
    }

    public ColumnEntity createColumn(String tableName, String fieldName) {
        ColumnEntity columnEntity = new ColumnEntity(tableName, fieldName);
        ColumnEntity save = columnRepository.save(columnEntity);
        return new ColumnEntity(
                save.getId(),
                save.isActive()
        );
    }

    public void setFieldRole(String fieldName, String tableName, String roleName) {
        logColumnCache.retrieveOrStore(tableName, fieldName).setRole(roleName);
    }
}
