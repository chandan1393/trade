package com.fyers.fyerstrading.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fyers.fyerstrading.entity.StockTrade;

@Repository
public interface StockTradeRepository extends JpaRepository<StockTrade, Long> {

    // Check if a stock has an open trade
    @Query("SELECT COUNT(s) > 0 FROM StockTrade s WHERE s.stockSymbol = :stockSymbol AND s.exitPrice = 0")
    boolean existsByStockSymbolAndExitPrice(@Param("stockSymbol") String stockSymbol);
    
    // Get the latest trade for a stock
    @Query(value = "SELECT * FROM stock_trades WHERE stock_symbol = :stockSymbol ORDER BY exit_date DESC LIMIT 1", nativeQuery = true)
    Optional<StockTrade> findLatestTrade(@Param("stockSymbol") String stockSymbol);
}