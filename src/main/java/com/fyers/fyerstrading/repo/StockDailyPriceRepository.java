package com.fyers.fyerstrading.repo;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fyers.fyerstrading.entity.FNO5MinCandle;
import com.fyers.fyerstrading.entity.NiftyDailyCandle;
import com.fyers.fyerstrading.entity.StockDailyPrice;

public interface StockDailyPriceRepository extends JpaRepository<StockDailyPrice, Long> {

	
	@Query("SELECT c FROM StockDailyPrice c WHERE c.stock.symbol = :symbol ORDER BY c.tradeDate DESC")
	List<StockDailyPrice> findTopNBySymbolOrderByTradeDateDesc(@Param("symbol") String symbol, Pageable pageable);

	default List<StockDailyPrice> findTopNByTradeDate(String symbol, int n) {
		List<StockDailyPrice> list= findTopNBySymbolOrderByTradeDateDesc(symbol, PageRequest.of(0, n));
		 Collections.reverse(list);
		    return list;
	}
	
	
	@Query("SELECT n FROM StockDailyPrice n WHERE n.stock.symbol = :symbol AND n.tradeDate = :tradeDate")
	StockDailyPrice findRecordOnDate(String symbol,@Param("tradeDate") LocalDate tradeDate);
	
    // ✅ Get all records for a stock, sorted by trade date ASC
    @Query("SELECT s FROM StockDailyPrice s WHERE  s.stock.symbol = :symbol ORDER BY s.tradeDate ASC")
    List<StockDailyPrice> findAllByStockSymbol(@Param("symbol") String symbol);

    // ✅ Get records after a specific date
    @Query("SELECT s FROM StockDailyPrice s WHERE  s.stock.symbol = :symbol AND s.tradeDate > :startDate ORDER BY s.tradeDate ASC")
    List<StockDailyPrice> findAfterDate(@Param("symbol") String symbol, @Param("startDate") LocalDate startDate);

    // ✅ Get records in a date range
    @Query("SELECT s FROM StockDailyPrice s WHERE  s.stock.symbol = :symbol AND s.tradeDate > :startDate AND s.tradeDate < :endDate ORDER BY s.tradeDate ASC")
    List<StockDailyPrice> findAllBWDate(@Param("symbol") String symbol, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // ✅ Get record for a specific trade date
    @Query("SELECT s FROM StockDailyPrice s WHERE  s.stock.symbol = :symbol AND DATE(s.tradeDate) = :tradeDate")
    Optional<StockDailyPrice> findByTradeDate(@Param("symbol") String symbol, @Param("tradeDate") LocalDate tradeDate);

    // ✅ Get the latest records using Pageable
    @Query("SELECT s FROM StockDailyPrice s WHERE  s.stock.symbol = :symbol ORDER BY s.tradeDate DESC")
    List<StockDailyPrice> findLatestByStockSymbol(@Param("symbol") String symbol, Pageable pageable);

    // ✅ Native Query to fetch latest records with a limit
    @Query(value = "SELECT * FROM stock_daily_data WHERE stock_symbol = :symbol ORDER BY trade_date DESC LIMIT :limit", nativeQuery = true)
    List<StockDailyPrice> findLatestDataNative(@Param("symbol") String symbol, 
    		@Param("limit") int limit);

    // ✅ Find top 10 records
    List<StockDailyPrice> findTop10ByStockSymbolOrderByTradeDateDesc(String stockSymbol);

    // ✅ Get latest valid record with non-null EMA, RSI, ATR, ADX values
    @Query("SELECT s FROM StockDailyPrice s WHERE  s.stock.symbol = :stockSymbol " +
           "AND s.technicalIndicator.ema9 IS NOT NULL AND s.technicalIndicator.ema10 IS NOT NULL AND s.technicalIndicator.ema14 IS NOT NULL " +
           "AND s.technicalIndicator.ema20 IS NOT NULL AND s.technicalIndicator.ema21 IS NOT NULL AND s.technicalIndicator.ema50 IS NOT NULL " +
           "AND s.technicalIndicator.ema200 IS NOT NULL AND s.technicalIndicator.rsi IS NOT NULL AND s.technicalIndicator.atr IS NOT NULL " +
           "ORDER BY s.tradeDate DESC")
    List<StockDailyPrice> findLatestValidRecord(@Param("stockSymbol") String stockSymbol, 
    		Pageable pageable);

    // ✅ Get records after a specific date
    @Query("SELECT s FROM StockDailyPrice s WHERE  s.stock.symbol = :stockSymbol " +
           "AND s.tradeDate > :lastTradeDate ORDER BY s.tradeDate ASC")
    List<StockDailyPrice> findRecordsAfterDate(@Param("stockSymbol") String stockSymbol, 
    		@Param("lastTradeDate") LocalDate lastTradeDate);

    // ✅ Get the latest record for a stock symbol
    @Query("SELECT s FROM StockDailyPrice s WHERE  s.stock.symbol = :symbol ORDER BY s.tradeDate DESC")
    Optional<StockDailyPrice> findLatestByStockSymbol(@Param("symbol") String symbol);

    @Query("SELECT d FROM StockDailyPrice d WHERE d.stock.symbol = :stockSymbol AND d.tradeDate <= :tradeDate ORDER BY d.tradeDate DESC")
    List<StockDailyPrice> findBeforeTradeDateDesc(@Param("stockSymbol") String stockSymbol,@Param("tradeDate") LocalDate tradeDate, Pageable pageable);
	
	default List<StockDailyPrice> findRecordsBeforeDate(@Param("stockSymbol")String stockSymbol,@Param("tradeDate") LocalDate tradeDate,int n){
		List<StockDailyPrice> list = findBeforeTradeDateDesc(stockSymbol,tradeDate,PageRequest.of(0, n));
		 Collections.reverse(list);
         return list;
	}
    
    
    
    
    
    @Query("SELECT d FROM StockDailyPrice d WHERE d.stock.id = :stockId ORDER BY d.tradeDate DESC")
    List<StockDailyPrice> findByStockIdOrderByTradeDateDesc(@Param("stockId") Long stockId, Pageable pageable);
	
	
	 @Query("SELECT s.tradeDate FROM StockDailyPrice s WHERE s.stock.symbol = :symbol")
	    Set<LocalDate> findExistingDatesBySymbol(@Param("symbol") String symbol);
	 
	 @Query(value = "SELECT s.trade_date FROM stock_daily_price s WHERE s.stock_id = :stockId ORDER BY s.trade_date ASC LIMIT 1", nativeQuery = true)
	 Optional<LocalDate> findStartDate(@Param("stockId") Long stockId);
	 
	 
		@Query("SELECT p FROM StockDailyPrice p " + "LEFT JOIN FETCH p.technicalIndicator "
				+ "WHERE p.stock.symbol = :symbol AND p.tradeDate >= :fromDate " + "ORDER BY p.tradeDate ASC")
		List<StockDailyPrice> findAllWithIndicatorsAfterDate(@Param("symbol") String symbol,
				@Param("fromDate") LocalDate fromDate);
		
		
		@Query("SELECT p FROM StockDailyPrice p " + "LEFT JOIN FETCH p.technicalIndicator "
				+ "WHERE p.stock.symbol = :symbol AND p.tradeDate >= :fromDate AND p.tradeDate<:toDate " + "ORDER BY p.tradeDate ASC")
		List<StockDailyPrice> findBTWDateWithIndicators(@Param("symbol") String symbol,
				@Param("fromDate") LocalDate fromDate,@Param("toDate") LocalDate toDate);
		
		
		
		@Query("SELECT MAX(s.tradeDate) FROM StockDailyPrice s WHERE s.stock.id = :stockId")
		LocalDate findLastTradeDateForStock(@Param("stockId") Long stockId);
		
		
		
		
		
	


	@Query("SELECT s FROM StockDailyPrice s WHERE s.tradeDate = :tradeDate "
			+ "AND s.deliveryPercent > :minDelivery AND s.deliveryPercent < :maxDelivery "
			+ "AND ((s.closePrice - s.openPrice) / s.openPrice) * 100 > :minMovePercent")
	List<StockDailyPrice> findRisingStocksWithDeliveryAndMovePercent(@Param("tradeDate") LocalDate tradeDate,
			@Param("minDelivery") Integer minDelivery, @Param("maxDelivery") Integer maxDelivery,
			@Param("minMovePercent") Double minMovePercent);
	
	@Query("SELECT s FROM StockDailyPrice s JOIN s.stock m " + "WHERE s.tradeDate = :tradeDate "
			+ "AND m.isInFno = true " + "AND s.deliveryPercent > :minDelivery "
			+ "AND s.deliveryPercent < :maxDelivery "
			+ "AND ((s.closePrice - s.openPrice) / s.openPrice) * 100 > :minMovePercent")
	List<StockDailyPrice> findRisingFnoStocksWithDeliveryAndMovePercent(@Param("tradeDate") LocalDate tradeDate,
			@Param("minDelivery") Integer minDelivery, @Param("maxDelivery") Integer maxDelivery,
			@Param("minMovePercent") Double minMovePercent);
	
	// Find latest trade date
    @Query("SELECT MAX(s.tradeDate) FROM StockDailyPrice s")
    LocalDate findLatestTradeDate();

    // Count records for a given date
    @Query("SELECT COUNT(s) FROM StockDailyPrice s WHERE s.tradeDate = :tradeDate")
    long countByTradeDate(@Param("tradeDate") LocalDate tradeDate);

    void deleteByTradeDate(LocalDate tradeDate);
    
    
    
    @Query("SELECT c FROM StockDailyPrice c where c.stock.symbol = :symbol AND c.tradeDate< :tradeDate ORDER BY c.tradeDate DESC")
	List<StockDailyPrice> findTopNByOrderByTradeDateDesc(@Param("symbol") String symbol,@Param("tradeDate") LocalDate tradeDate,Pageable pageable);
    
    
    
    /////////////////////////////////////////////unsued query
    
	@Query(value = "SELECT s.* FROM stock_daily_price s " + "INNER JOIN ( "
			+ "    SELECT stock_id, MAX(trade_date) AS max_date " + "    FROM stock_daily_price "
			+ "    WHERE stock_id IN (:stockIds) " + "    GROUP BY stock_id "
			+ ") latest ON latest.stock_id = s.stock_id AND latest.max_date = s.trade_date", nativeQuery = true)
	List<StockDailyPrice> findLatestPricesByStockIds(@Param("stockIds") List<Long> stockIds);
	
	
	boolean existsByStockIdAndTradeDate(Long stockId, LocalDate tradeDate);
	
	Optional<StockDailyPrice> findByStockIdAndTradeDate(Long stockId, LocalDate tradeDate);
	
	
	@Query("SELECT s.stock.id FROM StockDailyPrice s WHERE s.tradeDate = :date AND s.stock.id IN :stockIds")
	List<Long> findStockIdsWithTradeDate(@Param("date") LocalDate date, @Param("stockIds") List<Long> stockIds);
	
	
	List<StockDailyPrice> findByStockIdOrderByTradeDateAsc(Long stockId);
	
	
	@Query("SELECT s FROM StockDailyPrice s WHERE s.stock.id = :stockId AND s.tradeDate <= :tradeDate ORDER BY s.tradeDate DESC")
	List<StockDailyPrice> findTopByStockIdBeforeDate(@Param("stockId") Long stockId,
	                                                 @Param("tradeDate") LocalDate tradeDate,
	                                                 Pageable pageable);
	
	@Query("SELECT s FROM StockDailyPrice s WHERE s.stock.id IN :stockIds AND s.tradeDate < :tradeDate ORDER BY tradeDate DESC")
	List<StockDailyPrice> findAllByStockIdsBeforeDate(@Param("stockIds") List<Long> stockIds,
	                                                  @Param("tradeDate") LocalDate tradeDate);

}
 
