package com.fyers.fyerstrading.repo;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fyers.fyerstrading.entity.IndexOIData;

public interface IndexOIRepository extends JpaRepository<IndexOIData, Long> {

	// 1️⃣ Intraday data between time range (ordered)
	@Query("SELECT o FROM IndexOIData o " + "WHERE o.symbol = :symbol " + "AND o.timestamp >= :from "
			+ "AND o.timestamp < :to " + "ORDER BY o.timestamp")
	List<IndexOIData> findBySymbolAndDateBetween(@Param("symbol") String symbol, @Param("from") LocalDateTime from,
			@Param("to") LocalDateTime to);

	// 2️⃣ Last trading dates (used for backfill / analytics)
	@Query("SELECT DISTINCT CAST(o.timestamp AS date) " + "FROM IndexOIData o " + "WHERE o.symbol = :symbol "
			+ "AND o.timestamp < :now " + "ORDER BY CAST(o.timestamp AS date) DESC")
	List<java.sql.Date> findLastTradingDatesRaw(@Param("symbol") String symbol, @Param("now") LocalDateTime now,
			Pageable pageable);

	// 3️⃣ Latest row for a strike (very common in OI logic)
	IndexOIData findTopBySymbolAndStrikePriceOrderByTimestampDesc(String symbol, Double strikePrice);

	// 4️⃣ Latest available timestamp till now
	@Query("SELECT MAX(o.timestamp) " + "FROM IndexOIData o " + "WHERE o.symbol = :symbol " + "AND o.timestamp <= :now")
	LocalDateTime findLatestTimestamp(@Param("symbol") String symbol, @Param("now") LocalDateTime now);

	// 5️⃣ Previous timestamp before a given time
	@Query("SELECT MAX(o.timestamp) " + "FROM IndexOIData o " + "WHERE o.symbol = :symbol " + "AND o.timestamp < :time")
	LocalDateTime findPreviousTimestamp(@Param("symbol") String symbol, @Param("time") LocalDateTime time);

	// 6️⃣ Snapshot: all strikes at a specific timestamp
	List<IndexOIData> findBySymbolAndTimestamp(String symbol, LocalDateTime timestamp);

	@Query("select o from IndexOIData o where o.symbol = :symbol and o.timestamp = (select max(i.timestamp) from IndexOIData i where i.symbol = :symbol)")
	List<IndexOIData> findLatestBySymbol(@Param("symbol") String symbol);

	@Query("select o from IndexOIData o where o.symbol = :symbol and o.timestamp = (select max(i.timestamp) from IndexOIData i where i.symbol = :symbol)and o.strikePrice between :from and :to order by o.strikePrice ")
	List<IndexOIData> findLatestBetweenStrikes(@Param("symbol") String symbol, @Param("from") double from,
			@Param("to") double to);

	@Query("SELECT o FROM IndexOIData o WHERE o.timestamp = (SELECT MAX(x.timestamp) FROM IndexOIData x) ORDER BY o.strikePrice ASC")
	List<IndexOIData> findLatest();

}
