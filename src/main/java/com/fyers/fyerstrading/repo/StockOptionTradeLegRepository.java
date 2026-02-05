package com.fyers.fyerstrading.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fyers.fyerstrading.model.StockOptionTradeLeg;

public interface StockOptionTradeLegRepository extends JpaRepository<StockOptionTradeLeg, Long> {

	List<StockOptionTradeLeg> findByTradeId(Long tradeId);

	List<StockOptionTradeLeg> findByTradeIdIn(List<Long> tradeIds);
}
