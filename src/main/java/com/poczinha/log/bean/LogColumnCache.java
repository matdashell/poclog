package com.poczinha.log.bean;

import com.poczinha.log.domain.CompositeFieldKey;
import com.poczinha.log.hibernate.entity.ColumnEntity;
import com.poczinha.log.service.ColumnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class LogColumnCache {
    private final Map<CompositeFieldKey, ColumnEntity> columnEntityMap = new HashMap<>();

    @Autowired
    private ColumnService columnService;

    public ColumnEntity retrieveOrStore(String tableName, String columnName) {
        CompositeFieldKey compositeFieldKey = new CompositeFieldKey(columnName, tableName);
        return columnEntityMap.computeIfAbsent(compositeFieldKey, key -> columnService.columnEntityWithName(tableName, columnName));
    }
}
