package com.poczinha.log.bean;

import com.poczinha.log.hibernate.entity.ColumnEntity;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class LogColumnCache {
    private final Map<String, ColumnEntity> columnEntityMap;

    public LogColumnCache() {
        this.columnEntityMap = new HashMap<>();
    }

    public void put(String name, ColumnEntity columnEntity) {
        columnEntityMap.put(name, new ColumnEntity(columnEntity.getId()));
    }

    public ColumnEntity get(String name) {
        if (!columnEntityMap.containsKey(name)) return null;
        return columnEntityMap.get(name);
    }
}
