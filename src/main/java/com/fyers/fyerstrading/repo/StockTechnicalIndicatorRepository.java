package com.fyers.fyerstrading.repo;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fyers.fyerstrading.entity.StockMaster;
import com.fyers.fyerstrading.entity.StockTechnicalIndicator;

@Repository
public interface StockTechnicalIndicatorRepository extends JpaRepository<StockTechnicalIndicator, Long> {

	@Query("SELECT s FROM StockTechnicalIndicator s " + "JOIN s.stockDailyPrice d " + "WHERE d.stock.id = :stockId "
			+ "ORDER BY s.tradeDate DESC")
	List<StockTechnicalIndicator> findTopByStock(@Param("stockId") Long stockId, Pageable pageable);

	@Query("SELECT s FROM StockTechnicalIndicator s WHERE s.stockDailyPrice.stock = :stock AND s.tradeDate = :tradeDate")
	StockTechnicalIndicator findByStockAndTradeDate(@Param("stock") StockMaster stock,
			@Param("tradeDate") LocalDate tradeDate);

	@Query("SELECT s FROM StockTechnicalIndicator s WHERE s.stockDailyPrice.stock = :stock AND s.tradeDate < :tradeDate ORDER BY s.tradeDate DESC")
	List<StockTechnicalIndicator> findByStockAndPreviousTradeDate(@Param("stock") StockMaster stock,
			@Param("tradeDate") LocalDate tradeDate, Pageable pageable);

	@Query("SELECT i FROM StockTechnicalIndicator i JOIN FETCH i.stockDailyPrice "
			+ "    WHERE i.stockDailyPrice.stock = :stock  AND i.tradeDate < :currentDate"
			+ "    ORDER BY i.tradeDate DESC")
	List<StockTechnicalIndicator> findByStockAndPreviousTradeDateEagerLoading(@Param("stock") StockMaster stock,
			@Param("currentDate") LocalDate currentDate, Pageable pageable);

	@Query("SELECT sti FROM StockTechnicalIndicator sti WHERE sti.stockDailyPrice.stock = :stock ORDER BY sti.tradeDate ASC")
	List<StockTechnicalIndicator> findAllByStock(@Param("stock") StockMaster stock);

	/*
	 * @Query("SELECT sti FROM StockTechnicalIndicator sti " +
	 * "WHERE sti.stock.id IN :stockIds " + "AND sti.tradeDate = (" +
	 * "  SELECT MAX(sub.tradeDate) " + "  FROM StockTechnicalIndicator sub " +
	 * "  WHERE sub.stock.id = sti.stock.id " + "  AND sub.tradeDate < :date" + ")")
	 * List<StockTechnicalIndicator>
	 * findLatestIndicatorsByStocksAndDate(@Param("date") LocalDate date,
	 * 
	 * @Param("stockIds") List<Long> stockIds);
	 */

	@Query(value = "SELECT sti.* " + "FROM stock_technical_indicator sti "
			+ "INNER JOIN stock_daily_price sdp ON sti.daily_price_id = sdp.id " + "INNER JOIN ( "
			+ "  SELECT sdp2.stock_id, MAX(sdp2.trade_date) AS max_date " + "  FROM stock_technical_indicator sti2 "
			+ "  INNER JOIN stock_daily_price sdp2 ON sti2.daily_price_id = sdp2.id "
			+ "  WHERE sdp2.trade_date < :date AND sdp2.stock_id IN (:stockIds) " + "  GROUP BY sdp2.stock_id "
			+ ") latest ON latest.stock_id = sdp.stock_id AND latest.max_date = sdp.trade_date", nativeQuery = true)
	List<StockTechnicalIndicator> findLatestIndicatorsByStocksAndDate(@Param("date") LocalDate date,
			@Param("stockIds") List<Long> stockIds);

	@Query(value = "SELECT sti.* FROM stock_technical_indicator sti "
			+ "INNER JOIN stock_daily_price sdp ON sti.daily_price_id = sdp.id INNER JOIN ( "
			+ "    SELECT sub.stock_id, MAX(sub.trade_date) AS max_date FROM stock_daily_price sub "
			+ "    WHERE sub.trade_date < :currentDate AND sub.stock_id IN (:stockIds) "
			+ "    GROUP BY sub.stock_id " + ") latest "
			+ "ON sdp.stock_id = latest.stock_id AND sdp.trade_date = latest.max_date", nativeQuery = true)
	List<StockTechnicalIndicator> findLatestByTradeDateBefore(@Param("stockIds") List<Long> stockIds,
			@Param("currentDate") LocalDate currentDate);

	void deleteByTradeDate(LocalDate tradeDate);
}
