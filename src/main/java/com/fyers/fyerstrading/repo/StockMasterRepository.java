package com.fyers.fyerstrading.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fyers.fyerstrading.entity.StockMaster;

public interface StockMasterRepository extends JpaRepository<StockMaster, Long> {

	Optional<StockMaster> findBySymbol(String symbol);

	Optional<StockMaster> findByIsinCode(String isin);

	boolean existsByIsinCode(String isin);

	@Query("SELECT s FROM StockMaster s JOIN s.indices i WHERE i.symbol = :indexSymbol")
	List<StockMaster> findStocksByIndexSymbol(@Param("indexSymbol") String indexSymbol);
	
	@Query("SELECT s FROM StockMaster s JOIN s.indices i WHERE i.symbol IN :indexSymbols")
	List<StockMaster> findStocksByIndexSymbols(@Param("indexSymbols") List<String> indexSymbols);

	@Query(value = "SELECT * FROM stocks_master sm " + "WHERE sm.id NOT IN ("
			+ "SELECT sdp.stock_id FROM stock_daily_price sdp "
			+ "JOIN stock_technical_indicator sti ON sti.daily_price_id = sdp.id)", nativeQuery = true)
	List<StockMaster> findAllStocksWithoutIndicatorsNative();

	List<StockMaster> findByIsInFnoTrue();
	
	
	@Query(value = "SELECT sm.* FROM stocks_master sm " + "JOIN stock_daily_price sdp ON sdp.stock_id = sm.id "
			+ "JOIN stock_technical_indicator sti ON sti.daily_price_id = sdp.id " + "WHERE sti.atr IS NULL "
			+ "AND sdp.trade_date = (" + "SELECT MAX(inner_sdp.trade_date) " + "FROM stock_daily_price inner_sdp "
			+ "WHERE inner_sdp.stock_id = sm.id" + ")", nativeQuery = true)
	List<StockMaster> findAllStocksWithNullAtrOnLatestTradeDate();
	
	
	List<StockMaster> findBySymbolIn(List<String> symbols);


}