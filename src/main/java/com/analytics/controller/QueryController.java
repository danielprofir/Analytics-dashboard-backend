package com.analytics.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.analytics.dto.QueryRequest;
import com.analytics.dto.QueryResponse;
import com.analytics.entity.StoredQuery;
import com.analytics.service.QueryExecutionService;
import com.analytics.service.QueryService;

import jakarta.validation.Valid;

/**
 * REST controller for managing and executing analytical SQL queries.
 * Provides endpoints for storing queries, retrieving query lists, and executing them.
 */
@RestController
@RequestMapping("/api")
public class QueryController {
    
    @Autowired
    private QueryService queryService;
    
    @Autowired
    private QueryExecutionService queryExecutionService;
    
    /**
     * Saves a new query for later execution.
     * The query text is validated to ensure it only contains read-only operations.
     * 
     * @param request contains the SQL query text
     * @return the ID of the newly created query
     */
    @PostMapping("/queries")
    public ResponseEntity<QueryResponse> addQuery(@Valid @RequestBody QueryRequest request) {
        try {
            StoredQuery savedQuery = queryService.saveQuery(request.getQuery());
            return ResponseEntity.ok(new QueryResponse(savedQuery.getId()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Retrieves all stored queries with their IDs and query text.
     * 
     * @return list of all queries in the system
     */
    @GetMapping("/queries")
    public ResponseEntity<List<QueryResponse>> getAllQueries() {
        List<StoredQuery> queries = queryService.getAllQueries();
        List<QueryResponse> response = queries.stream()
            .map(q -> new QueryResponse(q.getId(), q.getQueryText()))
            .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
    
    /**
     * Executes a previously stored query and returns the results as a 2D array.
     * Results are cached since the underlying data doesn't change.
     * 
     * @param queryId the ID of the query to execute
     * @return 2D array containing the query results
     */
    @GetMapping("/execute")
    public ResponseEntity<Object[][]> executeQuery(@RequestParam("query") Long queryId) {
        try {
            Object[][] result = queryExecutionService.executeQuery(queryId);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}