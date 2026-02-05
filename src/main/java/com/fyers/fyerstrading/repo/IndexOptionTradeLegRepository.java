package com.fyers.fyerstrading.repo;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fyers.fyerstrading.entity.IndexOptionTradeLeg;
import com.fyers.fyerstrading.model.OptionTrade;

public interface IndexOptionTradeLegRepository extends JpaRepository<IndexOptionTradeLeg, Long> {

	List<IndexOptionTradeLeg> findByTradeId(Long tradeId);

	boolean existsByTradeId(Long tradeId);

	List<IndexOptionTradeLeg> findByTradeIdIn(List<Long> tradeIds);
}
