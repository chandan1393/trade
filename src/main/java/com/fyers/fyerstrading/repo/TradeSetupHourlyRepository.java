package com.fyers.fyerstrading.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.fyers.fyerstrading.entity.TradeSetupHourly;

public interface TradeSetupHourlyRepository extends JpaRepository<TradeSetupHourly, Long> {
	TradeSetupHourly findByEntryOrderId(String orderId);
    @Query("SELECT t FROM TradeSetupHourly t WHERE t.active = true")
    List<TradeSetupHourly> findActiveSetups();
}