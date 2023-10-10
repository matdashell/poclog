package com.poczinha.log.bean;

import com.poczinha.log.domain.CompositeFieldKey;
import com.poczinha.log.hibernate.entity.LogColumnEntity;
import com.poczinha.log.service.ColumnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class LogColumnCache {
    private final Map<CompositeFieldKey, LogColumnEntity> columnEntityMap = new HashMap<>();

    @Autowired
    private ColumnService columnService;

    public LogColumnEntity retrieveOrStore(String tableName, String columnName) {
        CompositeFieldKey compositeFieldKey = new CompositeFieldKey(columnName, tableName);
        return columnEntityMap.computeIfAbsent(compositeFieldKey, key -> columnService.retrieveOrStoreByTableAndColumn(tableName, columnName));
    }
}
