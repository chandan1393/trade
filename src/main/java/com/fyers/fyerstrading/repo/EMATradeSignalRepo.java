package com.fyers.fyerstrading.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fyers.fyerstrading.entity.EMATradeSignal;

public interface EMATradeSignalRepo extends JpaRepository<EMATradeSignal, Long> {

}
