package com.openecommerce.shared.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Abstract Aggregate Root
 * DDD聚合根基类
 */
@MappedSuperclass
@Getter
@EqualsAndHashCode(of = "id")
public abstract class AggregateRoot {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @Version
    private Long version;
    
    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    protected AggregateRoot() {
        // JPA required
    }
    
    public boolean isNew() {
        return this.id == null;
    }
}