package com.fyers.fyerstrading.repo;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fyers.fyerstrading.entity.TopMoversStock;

public interface TopMoversRepository extends JpaRepository<TopMoversStock, Long>{

	
	 @Query("SELECT COUNT(d) > 0 FROM TopMoversStock d WHERE d.symbol = :symbol AND d.tradeDate = :tradeDate")
	    boolean existsBySymbolAndTradeDate(@Param("symbol") String symbol, @Param("tradeDate") LocalDate tradeDate);
}
