package com.analytics.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

/**
 * Represents a stored SQL query that can be executed multiple times.
 * Tracks creation time, execution count, and last execution timestamp.
 */
@Entity
@Table(name = "stored_queries")
public class StoredQuery {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "query_text", columnDefinition = "TEXT")
    @NotBlank(message = "Query text cannot be empty")
    private String queryText;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "executed_count")
    private Integer executedCount = 0;
    
    @Column(name = "last_executed_at")
    private LocalDateTime lastExecutedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    // Constructors
    public StoredQuery() {}
    
    public StoredQuery(String queryText) {
        this.queryText = queryText;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getQueryText() {
        return queryText;
    }
    
    public void setQueryText(String queryText) {
        this.queryText = queryText;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public Integer getExecutedCount() {
        return executedCount;
    }
    
    public void setExecutedCount(Integer executedCount) {
        this.executedCount = executedCount;
    }
    
    public LocalDateTime getLastExecutedAt() {
        return lastExecutedAt;
    }
    
    public void setLastExecutedAt(LocalDateTime lastExecutedAt) {
        this.lastExecutedAt = lastExecutedAt;
    }
    
    /**
     * Increments the execution counter and updates the last executed timestamp.
     */
    public void incrementExecutedCount() {
        this.executedCount++;
        this.lastExecutedAt = LocalDateTime.now();
    }
}