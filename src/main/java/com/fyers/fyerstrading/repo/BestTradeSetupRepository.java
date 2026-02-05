package com.fyers.fyerstrading.repo;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fyers.fyerstrading.entity.BestTradeSetup;

@Repository
public interface BestTradeSetupRepository extends JpaRepository<BestTradeSetup, Long> {

    // Find setups for a specific day
    List<BestTradeSetup> findByTradeDate(LocalDate tradeDate);

    // Find unexecuted trades
    List<BestTradeSetup> findByTradeExecutedFalse();

    // Find specific stock setup
    Optional<BestTradeSetup> findByStockSymbolAndTradeDate(String stockSymbol, LocalDate tradeDate);
}
