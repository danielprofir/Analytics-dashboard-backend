package com.analytics.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

/**
 * Handles execution of stored queries against the database.
 * Uses a read-only connection to guarantee data integrity.
 */
@Service
public class QueryExecutionService {
    
    @Autowired
    @Qualifier("readOnlyJdbcTemplate")
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    private QueryService queryService;
    
    /**
     * Executes a stored query and returns results as a 2D array.
     * Results are cached by queryId since the data never changes.
     */
    @Cacheable(value = "queryResults", key = "#queryId")
    public Object[][] executeQuery(Long queryId) {
        var queryOpt = queryService.getQueryById(queryId);
        if (queryOpt.isEmpty()) {
            throw new IllegalArgumentException("Query with ID " + queryId + " not found");
        }
        
        String queryText = queryOpt.get().getQueryText();
        
        // Execute the query
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(queryText);
        
        // Update execution count
        queryService.incrementExecutionCount(queryId);
        
        if (rows.isEmpty()) {
            return new Object[0][0];
        }
        
        // Convert to 2D array
        String[] columnNames = rows.get(0).keySet().toArray(new String[0]);
        Object[][] result = new Object[rows.size()][columnNames.length];
        
        for (int i = 0; i < rows.size(); i++) {
            Map<String, Object> row = rows.get(i);
            for (int j = 0; j < columnNames.length; j++) {
                result[i][j] = row.get(columnNames[j]);
            }
        }
        
        return result;
    }
    
    /**
     * Runs a query directly without storing it first.
     * Mainly used for testing scenarios.
     */
    public Object[][] executeQueryDirect(String queryText) {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(queryText);
        
        if (rows.isEmpty()) {
            return new Object[0][0];
        }
        
        String[] columnNames = rows.get(0).keySet().toArray(new String[0]);
        Object[][] result = new Object[rows.size()][columnNames.length];
        
        for (int i = 0; i < rows.size(); i++) {
            Map<String, Object> row = rows.get(i);
            for (int j = 0; j < columnNames.length; j++) {
                result[i][j] = row.get(columnNames[j]);
            }
        }
        
        return result;
    }
}