package com.poczinha.log.domain.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.poczinha.log.domain.response.data.TableModification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@JsonPropertyOrder({"identifier", "correlation", "date", "tableModifications"})
public class CorrelationModification {
    private final String identifier;
    private final Long correlation;
    private final LocalDateTime date;

    private final List<TableModification> tableModifications = new ArrayList<>();

    public CorrelationModification(String identifier, Long correlation, LocalDateTime date) {
        this.identifier = identifier;
        this.correlation = correlation;
        this.date = date;
    }

    public String getIdentifier() {
        return identifier;
    }

    public Long getCorrelation() {
        return correlation;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public List<TableModification> getTableModifications() {
        return tableModifications;
    }
}
