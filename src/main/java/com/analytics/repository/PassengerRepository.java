package com.analytics.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.analytics.entity.Passenger;

/**
 * Repository for accessing Titanic passenger data.
 */
@Repository
public interface PassengerRepository extends JpaRepository<Passenger, Integer> {
}