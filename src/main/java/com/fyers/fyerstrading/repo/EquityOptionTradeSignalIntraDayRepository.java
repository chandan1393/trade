package com.fyers.fyerstrading.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fyers.fyerstrading.model.EquityOptionTradeSignalIntraday;

public interface EquityOptionTradeSignalIntraDayRepository extends JpaRepository<EquityOptionTradeSignalIntraday, Long> {

}
