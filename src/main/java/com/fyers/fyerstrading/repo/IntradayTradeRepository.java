package com.fyers.fyerstrading.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fyers.fyerstrading.entity.IntradayTrade;

public interface IntradayTradeRepository extends JpaRepository<IntradayTrade, Long>{

}
