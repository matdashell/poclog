package com.poczinha.log.domain;

import java.util.Objects;

public class CompositeFieldKey {
    private final String columnName;
    private final String tableName;

    public CompositeFieldKey(String columnName, String tableName) {
        this.columnName = columnName;
        this.tableName = tableName;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CompositeFieldKey that = (CompositeFieldKey) o;
        return Objects.equals(tableName, that.tableName) && Objects.equals(columnName, that.columnName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tableName, columnName);
    }

    public String getTableName() {
        return tableName;
    }

    public String getColumnName() {
        return columnName;
    }
}
