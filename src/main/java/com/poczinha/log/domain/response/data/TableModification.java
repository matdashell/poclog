package com.poczinha.log.domain.response.data;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.ArrayList;
import java.util.List;

@JsonPropertyOrder({"entityName", "groupsModifications"})
public class TableModification {
    private String entityName;
    private List<GroupTypeModification> groupsModifications = new ArrayList<>();

    public TableModification(String entityName) {
        this.entityName = entityName;
    }

    public String getTableName() {
        return entityName;
    }

    public void setTableName(String tableName) {
        this.entityName = tableName;
    }

    public List<GroupTypeModification> getGroupsModifications() {
        return groupsModifications;
    }

    public void setGroupsModifications(List<GroupTypeModification> groups) {
        this.groupsModifications = groups;
    }
}
