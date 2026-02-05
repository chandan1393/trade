package com.fyers.fyerstrading.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fyers.fyerstrading.entity.EMABacktestSignal;

public interface EMABacktestSignalRepo extends JpaRepository<EMABacktestSignal, Long> {

}
