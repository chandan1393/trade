package com.fyers.fyerstrading.repo;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fyers.fyerstrading.entity.TradeExecution;
import com.fyers.fyerstrading.entity.TradeStatus;
import java.time.LocalDate;

public interface TradeExecutionRepository extends JpaRepository<TradeExecution, Long> {

	Optional<TradeExecution> findByStockSymbolAndTradeSetup_Id(String stockSymbol, Long tradeSetupId);

	List<TradeExecution> findByEntryExecutedTrueAndTradeStatus(TradeStatus tradeStatus);

	@Query("SELECT t FROM TradeExecution t WHERE t.entryExecuted = true AND t.tradeStatus IN :statuses")
	List<TradeExecution> findByEntryExecutedAndActiveStatuses(@Param("statuses") Set<TradeStatus> statuses);

	@Query("SELECT t FROM TradeExecution t WHERE t.fullyExited = false")
	List<TradeExecution> findAllActive();
	

	@Query("SELECT t FROM TradeExecution t WHERE t.stockSymbol = :symbol AND t.positionSize = :qty AND t.tradeStatus = :tradeStatus")
	Optional<TradeExecution> findBySymbolQtyAndStatus(@Param("symbol") String symbol, @Param("qty") int qty,
			@Param("tradeStatus") TradeStatus status);

	
	@Query("SELECT t FROM TradeExecution t WHERE t.tradeStatus = :tradeStatus")
	List<TradeExecution> findByTradeStatus(@Param("tradeStatus") TradeStatus status);
	
	
	@Query("SELECT t FROM TradeExecution t WHERE t.stockSymbol = :symbol AND t.tradeStatus IN :statuses")
	Optional<TradeExecution> findBySymbolAndStatus(@Param("symbol") String symbol,
			@Param("statuses") Set<TradeStatus> statuses);

}
