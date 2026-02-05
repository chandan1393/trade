package com.fyers.fyerstrading.repo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fyers.fyerstrading.entity.TradeHistory;

@Repository
public interface TradeHistoryRepository extends JpaRepository<TradeHistory, Long> {
}
 