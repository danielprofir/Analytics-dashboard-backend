package com.analytics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * Main entry point for the Analytics Backend application.
 * Provides REST APIs for storing and executing analytical SQL queries.
 */
@SpringBootApplication
@EnableCaching
public class AnalyticsBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(AnalyticsBackendApplication.class, args);
    }
}