package com.fyers.fyerstrading.repo;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fyers.fyerstrading.entity.IntradayLivePosition;


public interface IntradayLivePositionRepository extends JpaRepository<IntradayLivePosition , Long> {

	Optional<IntradayLivePosition> findBySpotSymbolAndTradeDate(String symbol, LocalDate today);

}
