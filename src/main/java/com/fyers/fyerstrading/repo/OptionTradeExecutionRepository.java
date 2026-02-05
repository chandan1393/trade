package com.fyers.fyerstrading.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fyers.fyerstrading.entity.OptionTradeExecution;

public interface OptionTradeExecutionRepository extends JpaRepository<OptionTradeExecution, Long> {
    List<OptionTradeExecution> findByExitTimeIsNull();
}
