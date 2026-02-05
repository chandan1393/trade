package com.fyers.fyerstrading.repo;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fyers.fyerstrading.entity.TradeSignalEntity;

public interface TradeSignalRepository extends JpaRepository<TradeSignalEntity, Long> {

	@Modifying
	@Query("UPDATE TradeSignalEntity t SET t.oiChange = :oiChange, t.oiChangePercent = :oiChangePercent, t.volume = :volume, t.ltp = :ltp, t.iv = :iv, t.signalTime = :signalTime WHERE t.symbol = :symbol AND t.strikePrice = :strikePrice AND t.expiryDate = :expiryDate")
	void updateTradeSignal(@Param("oiChange") double oiChange, @Param("oiChangePercent") double oiChangePercent, 
	                       @Param("volume") double volume, @Param("ltp") double ltp, @Param("iv") double iv, 
	                       @Param("signalTime") LocalDateTime signalTime, @Param("symbol") String symbol, 
	                       @Param("strikePrice") double strikePrice, @Param("expiryDate") String expiryDate);

	@Modifying
	
	@Query("DELETE FROM TradeSignalEntity t WHERE t.signalTime < :cutoffTime")
	void deleteOldTradeSignals(@Param("cutoffTime") LocalDateTime cutoffTime);
	
	
	@Modifying
	@Query("DELETE FROM TradeSignalEntity t WHERE t.expiryDate < :today")
	void deleteExpiredContracts(@Param("today") String today);

}
