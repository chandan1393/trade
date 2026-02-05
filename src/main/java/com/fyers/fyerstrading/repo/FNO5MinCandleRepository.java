package com.fyers.fyerstrading.repo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fyers.fyerstrading.entity.FNO5MinCandle;
import com.fyers.fyerstrading.entity.Nifty5MinCandle;

public interface FNO5MinCandleRepository extends JpaRepository<FNO5MinCandle, Long> {

	@Query("SELECT c FROM FNO5MinCandle c WHERE c.symbol = :symbol ORDER BY c.timestamp DESC")
	List<FNO5MinCandle> findTopNBySymbolOrderByTimestampDesc(@Param("symbol") String symbol, Pageable pageable);

	default List<FNO5MinCandle> findTopNByTimestamp(String symbol, int n) {
		List<FNO5MinCandle> list = findTopNBySymbolOrderByTimestampDesc(symbol, PageRequest.of(0, n));
		Collections.reverse(list);
		return list;
	}

	@Query("SELECT n FROM FNO5MinCandle n WHERE n.symbol = :symbol AND n.timestamp = :timestamp")
	FNO5MinCandle findRecordOnTime(@Param("symbol") String symbol, @Param("timestamp") LocalDateTime timestamp);

	@Query(" SELECT c FROM FNO5MinCandle c WHERE c.symbol=:symbol AND c.timestamp>=:start AND c.timestamp<=:end"
	+"  ORDER BY c.timestamp ASC")

	List<FNO5MinCandle> findAllBWDate(@Param("symbol") String symbol, @Param("start") LocalDateTime start,
			@Param("end") LocalDateTime end);
	Optional<FNO5MinCandle> findTop1BySymbolOrderByTimestampDesc(String symbol);

	// fetch last N candles by symbol, newest first

	@Query("SELECT c FROM FNO5MinCandle c WHERE c.symbol = :symbol AND c.timestamp <= :uptoTime ORDER BY c.timestamp DESC")
	List<FNO5MinCandle> findLastNCandlesBefore(@Param("symbol") String symbol,
			@Param("uptoTime") LocalDateTime uptoTime, Pageable pageable);

	default List<FNO5MinCandle> findLastNCandlesBefore(String symbol, LocalDateTime uptoTime, int n) {
		return findLastNCandlesBefore(symbol, uptoTime, PageRequest.of(0, n));
	}

	boolean existsBySymbolAndTimestamp(String symbol, LocalDateTime timestamp);

	@Query(value = "SELECT DISTINCT DATE(timestamp) FROM fno_5min_candles WHERE symbol = :symbol"
			+ " AND timestamp < :date ORDER BY DATE(timestamp) DESC", nativeQuery = true)
	List<java.sql.Date> findDistinctDatesBySymbolBefore(@Param("symbol") String symbol,
			@Param("date") LocalDateTime date);

	@Query(value = "SELECT * FROM fno_5min_candles WHERE symbol = :symbol AND DATE(timestamp) = :date "
			+ "ORDER BY timestamp ASC", nativeQuery = true)
	List<FNO5MinCandle> getCandlesByDate(@Param("symbol") String symbol, @Param("date") LocalDate date);
}
