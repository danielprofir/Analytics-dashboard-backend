package com.analytics.config;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DelegatingDataSource;

/**
 * Configures database connections for query execution.
 * Creates a read-only datasource that enforces data safety at the database level.
 */
@Configuration
public class DataSourceConfig {
    
    @Autowired
    private DataSource dataSource;
    
    /**
     * Creates a JdbcTemplate with read-only connection for query execution.
     * This GUARANTEES that queries cannot modify data - enforced by the database.
     */
    @Bean(name = "readOnlyJdbcTemplate")
    public JdbcTemplate readOnlyJdbcTemplate() {
        // Create a wrapper DataSource that sets connections to read-only
        DataSource readOnlyDataSource = new DelegatingDataSource(dataSource) {
            @Override
            public Connection getConnection() throws SQLException {
                Connection connection = super.getConnection();
                connection.setReadOnly(true); // This is the guarantee!
                return connection;
            }
            
            @Override
            public Connection getConnection(String username, String password) throws SQLException {
                Connection connection = super.getConnection(username, password);
                connection.setReadOnly(true);
                return connection;
            }
        };
        
        return new JdbcTemplate(readOnlyDataSource);
    }
}
