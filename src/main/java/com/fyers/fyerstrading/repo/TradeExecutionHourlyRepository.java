package com.fyers.fyerstrading.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fyers.fyerstrading.entity.TradeExecutionHourly;

public interface TradeExecutionHourlyRepository extends JpaRepository<TradeExecutionHourly, Long> {}