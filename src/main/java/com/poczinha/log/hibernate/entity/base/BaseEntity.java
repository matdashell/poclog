package com.poczinha.log.hibernate.entity.base;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@MappedSuperclass
public class BaseEntity {
    private LocalDateTime createdAt;
    private boolean visible;

    public void register() {
        this.createdAt = LocalDateTime.now();
        this.visible = false;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
