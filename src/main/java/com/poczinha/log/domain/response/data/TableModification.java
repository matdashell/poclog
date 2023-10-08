package com.poczinha.log.domain.response.data;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.ArrayList;
import java.util.List;

@JsonPropertyOrder({"tableName", "groups"})
public class TableModification {
    private String tableName;
    private List<GroupTypeModification> groups = new ArrayList<>();

    public TableModification(String tableName) {
        this.tableName = tableName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public List<GroupTypeModification> getGroups() {
        return groups;
    }

    public void setGroups(List<GroupTypeModification> groups) {
        this.groups = groups;
    }
}
