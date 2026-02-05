package com.fyers.fyerstrading.repo;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fyers.fyerstrading.entity.DeliverySpikeStock;
import com.fyers.fyerstrading.entity.StockDailyPrice;

@Repository
public interface DeliverySpikeStockRepository extends JpaRepository<DeliverySpikeStock, Long> {
    List<DeliverySpikeStock> findByTradeDate(LocalDate tradeDate);
    
    @Query("SELECT COUNT(d) > 0 FROM DeliverySpikeStock d WHERE d.symbol = :symbol AND d.tradeDate = :tradeDate")
    boolean existsBySymbolAndTradeDate(@Param("symbol") String symbol, @Param("tradeDate") LocalDate tradeDate);

    Optional<DeliverySpikeStock> findTopByOrderByTradeDateDesc();
}
