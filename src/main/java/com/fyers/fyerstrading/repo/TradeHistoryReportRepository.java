package com.fyers.fyerstrading.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fyers.fyerstrading.entity.TradeHistoryReport;

public interface TradeHistoryReportRepository extends JpaRepository<TradeHistoryReport, Long>{

}
