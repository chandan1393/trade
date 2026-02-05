package com.fyers.fyerstrading.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fyers.fyerstrading.entity.FyersAuthDetails;

@Repository
public interface FyersAuthRepository extends JpaRepository<FyersAuthDetails, Long> {
    FyersAuthDetails findByClientId(String clientId);
}