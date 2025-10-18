package com.analytics.service;

import com.analytics.entity.StoredQuery;
import com.analytics.repository.StoredQueryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QueryServiceTest {
    
    @Mock
    private StoredQueryRepository storedQueryRepository;
    
    @InjectMocks
    private QueryService queryService;
    
    private StoredQuery testQuery;
    
    @BeforeEach
    void setUp() {
        testQuery = new StoredQuery("SELECT * FROM passengers");
        testQuery.setId(1L);
    }
    
    @Test
    void saveQuery_ValidSelectQuery_Success() {
        // Given
        String queryText = "SELECT * FROM passengers";
        when(storedQueryRepository.save(any(StoredQuery.class))).thenReturn(testQuery);
        
        // When
        StoredQuery result = queryService.saveQuery(queryText);
        
        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(storedQueryRepository).save(any(StoredQuery.class));
    }
    
    @Test
    void saveQuery_InsertQuery_ThrowsException() {
        // Given
        String dangerousQuery = "INSERT INTO passengers VALUES (999, 1, 1, 'Test', 'male', 25, 0, 0, 'T123', 10.0, 'A1', 'S')";
        
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> queryService.saveQuery(dangerousQuery)
        );
        
        assertTrue(exception.getMessage().contains("dangerous operations"));
        verify(storedQueryRepository, never()).save(any());
    }
    
    @Test
    void saveQuery_UpdateQuery_ThrowsException() {
        // Given
        String dangerousQuery = "UPDATE passengers SET age = 30 WHERE passenger_id = 1";
        
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> queryService.saveQuery(dangerousQuery)
        );
        
        assertTrue(exception.getMessage().contains("dangerous operations"));
    }
    
    @Test
    void saveQuery_DeleteQuery_ThrowsException() {
        // Given
        String dangerousQuery = "DELETE FROM passengers WHERE passenger_id = 1";
        
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> queryService.saveQuery(dangerousQuery)
        );
        
        assertTrue(exception.getMessage().contains("dangerous operations"));
    }
    
    @Test
    void getAllQueries_ReturnsAllQueries() {
        // Given
        List<StoredQuery> queries = Arrays.asList(testQuery);
        when(storedQueryRepository.findAll()).thenReturn(queries);
        
        // When
        List<StoredQuery> result = queryService.getAllQueries();
        
        // Then
        assertEquals(1, result.size());
        assertEquals(testQuery, result.get(0));
    }
    
    @Test
    void getQueryById_ExistingId_ReturnsQuery() {
        // Given
        when(storedQueryRepository.findById(1L)).thenReturn(Optional.of(testQuery));
        
        // When
        Optional<StoredQuery> result = queryService.getQueryById(1L);
        
        // Then
        assertTrue(result.isPresent());
        assertEquals(testQuery, result.get());
    }
    
    @Test
    void getQueryById_NonExistingId_ReturnsEmpty() {
        // Given
        when(storedQueryRepository.findById(999L)).thenReturn(Optional.empty());
        
        // When
        Optional<StoredQuery> result = queryService.getQueryById(999L);
        
        // Then
        assertFalse(result.isPresent());
    }
    
    @Test
    void incrementExecutionCount_ExistingQuery_IncrementsCount() {
        // Given
        testQuery.setExecutedCount(5);
        when(storedQueryRepository.findById(1L)).thenReturn(Optional.of(testQuery));
        when(storedQueryRepository.save(any(StoredQuery.class))).thenReturn(testQuery);
        
        // When
        queryService.incrementExecutionCount(1L);
        
        // Then
        verify(storedQueryRepository).save(testQuery);
        assertEquals(6, testQuery.getExecutedCount());
        assertNotNull(testQuery.getLastExecutedAt());
    }
}