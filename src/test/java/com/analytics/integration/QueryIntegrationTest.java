package com.analytics.integration;

import com.analytics.dto.QueryRequest;
import com.analytics.dto.QueryResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
class QueryIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Test
    void fullQueryFlow_AddListExecute_Success() throws Exception {
        // Step 1: Add a query
        QueryRequest request = new QueryRequest("SELECT COUNT(*) FROM passengers");
        
        MvcResult addResult = mockMvc.perform(post("/api/queries")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andReturn();
        
        String responseContent = addResult.getResponse().getContentAsString();
        QueryResponse queryResponse = objectMapper.readValue(responseContent, QueryResponse.class);
        Long queryId = queryResponse.getId();
        assertNotNull(queryId);
        
        // Step 2: List all queries and verify our query is there
        mockMvc.perform(get("/api/queries"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[?(@.id == " + queryId + ")].query").value("SELECT COUNT(*) FROM passengers"));
        
        // Step 3: Execute the query
        MvcResult executeResult = mockMvc.perform(get("/api/execute")
                .param("query", queryId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0]").isArray())
                .andReturn();
        
        String executeContent = executeResult.getResponse().getContentAsString();
        assertTrue(executeContent.contains("891")); // Expected passenger count in Titanic dataset
    }
    
    @Test
    void executeQuery_ComplexQuery_ReturnsCorrectResults() throws Exception {
        // Add a more complex query
        QueryRequest request = new QueryRequest(
            "SELECT sex, COUNT(*) as count FROM passengers GROUP BY sex ORDER BY sex"
        );
        
        MvcResult addResult = mockMvc.perform(post("/api/queries")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();
        
        String responseContent = addResult.getResponse().getContentAsString();
        QueryResponse queryResponse = objectMapper.readValue(responseContent, QueryResponse.class);
        Long queryId = queryResponse.getId();
        
        // Execute the query
        mockMvc.perform(get("/api/execute")
                .param("query", queryId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0]").isArray())
                .andExpect(jsonPath("$[0][0]").value("female"))
                .andExpect(jsonPath("$[1][0]").value("male"));
    }
    
    @Test
    void addQuery_DangerousQuery_ReturnsError() throws Exception {
        QueryRequest request = new QueryRequest("DROP TABLE passengers");
        
        mockMvc.perform(post("/api/queries")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void executeQuery_NonExistentId_ReturnsNotFound() throws Exception {
        mockMvc.perform(get("/api/execute")
                .param("query", "99999"))
                .andExpect(status().isNotFound());
    }
}