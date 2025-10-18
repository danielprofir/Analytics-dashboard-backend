package com.analytics.controller;

import com.analytics.dto.QueryRequest;
import com.analytics.entity.StoredQuery;
import com.analytics.service.QueryExecutionService;
import com.analytics.service.QueryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(QueryController.class)
class QueryControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private QueryService queryService;
    
    @MockBean
    private QueryExecutionService queryExecutionService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Test
    void addQuery_ValidQuery_ReturnsQueryId() throws Exception {
        // Given
        QueryRequest request = new QueryRequest("SELECT * FROM passengers");
        StoredQuery savedQuery = new StoredQuery("SELECT * FROM passengers");
        savedQuery.setId(1L);
        when(queryService.saveQuery(any(String.class))).thenReturn(savedQuery);
        
        // When & Then
        mockMvc.perform(post("/api/queries")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }
    
    @Test
    void addQuery_InvalidQuery_ReturnsBadRequest() throws Exception {
        // Given
        QueryRequest request = new QueryRequest("DELETE FROM passengers");
        when(queryService.saveQuery(any(String.class))).thenThrow(new IllegalArgumentException("Dangerous operation"));
        
        // When & Then
        mockMvc.perform(post("/api/queries")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void getAllQueries_ReturnsQueryList() throws Exception {
        // Given
        StoredQuery query1 = new StoredQuery("SELECT * FROM passengers");
        query1.setId(1L);
        StoredQuery query2 = new StoredQuery("SELECT COUNT(*) FROM passengers");
        query2.setId(2L);
        
        List<StoredQuery> queries = Arrays.asList(query1, query2);
        when(queryService.getAllQueries()).thenReturn(queries);
        
        // When & Then
        mockMvc.perform(get("/api/queries"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].query").value("SELECT * FROM passengers"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].query").value("SELECT COUNT(*) FROM passengers"));
    }
    
    @Test
    void executeQuery_ValidQueryId_ReturnsResults() throws Exception {
        // Given
        Object[][] results = {
            {1, 0, 3, "Braund, Mr. Owen Harris", "male"},
            {2, 1, 1, "Cumings, Mrs. John Bradley", "female"}
        };
        when(queryExecutionService.executeQuery(1L)).thenReturn(results);
        
        // When & Then
        mockMvc.perform(get("/api/execute").param("query", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0][0]").value(1))
                .andExpect(jsonPath("$[0][4]").value("male"))
                .andExpect(jsonPath("$[1][0]").value(2))
                .andExpect(jsonPath("$[1][4]").value("female"));
    }
    
    @Test
    void executeQuery_InvalidQueryId_ReturnsNotFound() throws Exception {
        // Given
        when(queryExecutionService.executeQuery(anyLong()))
                .thenThrow(new IllegalArgumentException("Query not found"));
        
        // When & Then
        mockMvc.perform(get("/api/execute").param("query", "999"))
                .andExpect(status().isNotFound());
    }
}