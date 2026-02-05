package com.fyers.fyerstrading.repo;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.fyers.fyerstrading.entity.TradeSetupForFNO;

@Repository
public interface TradeSetupFNORepository extends JpaRepository<TradeSetupForFNO, Long> {

	@Query("SELECT MAX(t.tradeFoundDate) FROM TradeSetupForFNO t")
    LocalDate findLatestTradeDate();

}
