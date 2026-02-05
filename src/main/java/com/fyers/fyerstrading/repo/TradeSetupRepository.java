package com.fyers.fyerstrading.repo;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fyers.fyerstrading.model.TradeSetupAndExecution;
import com.fyers.fyerstrading.entity.TradeExecution;
import com.fyers.fyerstrading.entity.TradeSetup;
import com.fyers.fyerstrading.entity.TradeStatus;


@Repository
public interface TradeSetupRepository extends JpaRepository<TradeSetup, Long> {
	@Query("SELECT t FROM TradeSetup t WHERE t.tradeEntered = :tradeEntered AND t.isActive = true AND t.tradeStatus = 'SETUP_FOUND'")
	List<TradeSetup> findActiveFoundSetupsByTradeEntered(@Param("tradeEntered") boolean tradeEntered);
	
	
	@Query("SELECT t FROM TradeSetup t WHERE t.isActive = true AND t.tradeStatus = 'SETUP_FOUND'")
	List<TradeSetup> findActiveFoundSetupsByTradeEntered();
	
	@Query("SELECT ts FROM TradeSetup ts WHERE ts.isActive = true AND ts.tradeEntered = false "
			+ "AND ts.tradeStatus IN ('SETUP_FOUND', 'PENDING_ENTRY')")
	List<TradeSetup> findUnexecutedAndPendingEntrySetups();
	
	@Query("SELECT ts FROM TradeSetup ts WHERE ts.isActive = true AND ts.tradeEntered = false "
		     + "AND ts.tradeStatus IN ('SETUP_FOUND', 'PENDING_ENTRY') "
		     + "AND ts.tradeFoundDate < :date")
		List<TradeSetup> findUnexecutedAndPendingEntrySetupsBeforeDate(@Param("date") LocalDate date);
	
	
	@Query("SELECT t FROM TradeSetup t WHERE t.isActive = true AND t.tradeEntered = false")
	List<TradeSetup> findActiveSetupsNotEntered();
	
	List<TradeSetup> findByDeliveryPercentEquals(int deliveryPercent);
	
	
	@Query("SELECT MAX(t.tradeFoundDate) FROM TradeSetup t")
    LocalDate findLatestTradeDate();


	@Query("SELECT t FROM TradeSetup t WHERE t.stockSymbol = :symbol AND t.positionSize = :qty AND t.tradeStatus = :tradeStatus")
	Optional<TradeSetup> findBySymbolQtyAndStatus(@Param("symbol") String symbol, @Param("qty") int qty,
			@Param("tradeStatus") TradeStatus status);
	
	@Query("SELECT t FROM TradeSetup t WHERE  t.tradeStatus = :tradeStatus")
	List<TradeSetup> findByStatus(@Param("tradeStatus") TradeStatus status);
	
	List<TradeExecution> findByGttEntryOrderIdIn(List<String> gttOrderIds);


}

