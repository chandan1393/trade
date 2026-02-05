package com.fyers.fyerstrading.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fyers.fyerstrading.entity.CandleEntity;

public interface CandleRepository extends JpaRepository<CandleEntity, Long>{

}
