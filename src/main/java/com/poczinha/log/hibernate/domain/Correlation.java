package com.poczinha.log.hibernate.domain;

import java.time.LocalDateTime;
import java.util.UUID;

public class Correlation {
    private final String id = UUID.randomUUID().toString();
    private final LocalDateTime date = LocalDateTime.now();

    public String getId() {
        return id;
    }

    public LocalDateTime getDate() {
        return date;
    }
}
