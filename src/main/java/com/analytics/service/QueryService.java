package com.analytics.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.analytics.entity.StoredQuery;
import com.analytics.repository.StoredQueryRepository;

/**
 * Service layer for managing stored queries.
 * Handles query validation, persistence, and execution tracking.
 */
@Service
public class QueryService {
    
    @Autowired
    private StoredQueryRepository storedQueryRepository;
    
    /**
     * Validates and saves a query for future execution.
     * 
     * @param queryText the SQL query to store
     * @return the saved query entity with generated ID
     * @throws IllegalArgumentException if the query contains dangerous operations
     */
    @Transactional
    public StoredQuery saveQuery(String queryText) {
        validateQuerySafety(queryText);
        StoredQuery query = new StoredQuery(queryText);
        return storedQueryRepository.save(query);
    }
    
    /**
     * Fetches all stored queries from the database.
     */
    public List<StoredQuery> getAllQueries() {
        return storedQueryRepository.findAll();
    }
    
    /**
     * Looks up a query by its ID.
     */
    public Optional<StoredQuery> getQueryById(Long id) {
        return storedQueryRepository.findById(id);
    }
    
    /**
     * Updates the execution count and timestamp for a query.
     * Called each time a query is successfully executed.
     */
    @Transactional
    public void incrementExecutionCount(Long id) {
        Optional<StoredQuery> queryOpt = storedQueryRepository.findById(id);
        if (queryOpt.isPresent()) {
            StoredQuery query = queryOpt.get();
            query.incrementExecutedCount();
            storedQueryRepository.save(query);
        }
    }
    
    /**
     * Checks if a query contains dangerous SQL operations.
     * This provides the first layer of protection against data modification,
     * with the read-only connection providing the ultimate guarantee.
     */
    private void validateQuerySafety(String queryText) {
        String normalizedQuery = queryText.trim().toLowerCase();
        
        // List of SQL keywords that modify data
        String[] dangerousKeywords = {
            "insert", "update", "delete", "drop", "create", "alter", 
            "truncate", "replace", "merge", "call", "exec"
        };
        
        for (String keyword : dangerousKeywords) {
            if (normalizedQuery.startsWith(keyword + " ") || 
                normalizedQuery.contains(" " + keyword + " ") ||
                normalizedQuery.contains(";" + keyword + " ")) {
                throw new IllegalArgumentException(
                    "Query contains potentially dangerous operations. Only SELECT queries are allowed."
                );
            }
        }
    }
}