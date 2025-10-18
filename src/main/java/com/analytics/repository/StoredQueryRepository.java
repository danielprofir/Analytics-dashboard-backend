package com.analytics.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.analytics.entity.StoredQuery;

/**
 * Data access layer for stored queries.
 */
@Repository
public interface StoredQueryRepository extends JpaRepository<StoredQuery, Long> {
}