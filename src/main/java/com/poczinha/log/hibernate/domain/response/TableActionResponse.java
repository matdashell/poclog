package com.poczinha.log.hibernate.domain.response;

import java.util.List;

public class TableActionResponse {

    private final String name;
    private final List<ColumnActionResponse> columnActionResponses;

    public TableActionResponse(String name, List<ColumnActionResponse> columnActionResponses) {
        this.name = name;
        this.columnActionResponses = columnActionResponses;
    }

    public String getName() {
        return name;
    }

    public List<ColumnActionResponse> getColumnActionResponses() {
        return columnActionResponses;
    }
}
