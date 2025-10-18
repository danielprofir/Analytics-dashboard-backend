package com.analytics.dto;

/**
 * Response object containing query details.
 * Can include just the ID (for create operations) or both ID and query text (for list operations).
 */
public class QueryResponse {
    private Long id;
    private String query;
    
    public QueryResponse() {}
    
    /**
     * Creates a response with just the query ID.
     */
    public QueryResponse(Long id) {
        this.id = id;
    }
    
    /**
     * Creates a response with both ID and query text.
     */
    public QueryResponse(Long id, String query) {
        this.id = id;
        this.query = query;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getQuery() {
        return query;
    }
    
    public void setQuery(String query) {
        this.query = query;
    }
}