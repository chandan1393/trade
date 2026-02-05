package com.fyers.fyerstrading.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.fyers.fyerstrading.entity.ActiveTrades;

@Repository
public interface ActiveTradesRepository extends JpaRepository<ActiveTrades, Long> {
    List<ActiveTrades> findByStockSymbol(String stockSymbol);
    
    @Query("SELECT s FROM ActiveTrades s WHERE s.isClosed =false ORDER BY s.entryTime")
    List<ActiveTrades> findAllActiveTrades();
    
    
}
