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
import com.fyers.fyerstrading.entity.NiftyDailyCandle;

public interface NiftyFiveMinCandleRepository extends JpaRepository<Nifty5MinCandle, Long> {

	@Query("SELECT c FROM Nifty5MinCandle c ORDER BY c.timestamp DESC")
	List<Nifty5MinCandle> findTopNByOrderByTimestampDesc(Pageable pageable);

	default List<Nifty5MinCandle> findTopNByTimestamp(int n) {
		List<Nifty5MinCandle> list = findTopNByOrderByTimestampDesc(PageRequest.of(0, n));
		Collections.reverse(list);
		return list;
	}

	@Query("SELECT n FROM Nifty5MinCandle n WHERE n.timestamp = :timestamp")
	Nifty5MinCandle findRecordOnTime(@Param("timestamp") LocalDateTime timestamp);

	@Query("SELECT MIN(n.timestamp) FROM Nifty5MinCandle n")
	Optional<LocalDate> findOldestDate();

	Optional<Nifty5MinCandle> findTop1ByOrderByTimestampDesc();

	@Query("SELECT c FROM Nifty5MinCandle c WHERE c.timestamp BETWEEN :start AND :end")
	List<Nifty5MinCandle> findAllBWDate(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

	List<Nifty5MinCandle> findAllByOrderByTimestampAsc();

	boolean existsByTimestamp(LocalDateTime timestamp);

	// 1. Find the last candle where EMA values are already calculated
	Nifty5MinCandle findTopByEma9IsNotNullAndEma21IsNotNullOrderByTimestampDesc();

	@Query(value = "SELECT * FROM nifty_5min_candle WHERE DATE(timestamp) = :date "
			+ "ORDER BY timestamp ASC", nativeQuery = true)
	List<Nifty5MinCandle> getCandlesByDate(@Param("date") LocalDate date);

	List<Nifty5MinCandle> findTop2ByOrderByTimestampDesc();

	@Query("SELECT c FROM Nifty5MinCandle c WHERE  c.timestamp <= :uptoTime ORDER BY c.timestamp DESC")
	List<Nifty5MinCandle> findLastNCandlesBefore(@Param("uptoTime") LocalDateTime uptoTime, Pageable pageable);

	default List<Nifty5MinCandle> findLastNCandlesBefore(LocalDateTime uptoTime, int n) {
		return findLastNCandlesBefore(uptoTime, PageRequest.of(0, n));
	}

	@Query("SELECT c FROM Nifty5MinCandle c WHERE c.timestamp BETWEEN :start AND :end")
	List<Nifty5MinCandle> findAllBetweenTimestamps(@Param("start") LocalDateTime start,
			@Param("end") LocalDateTime end);

	@Query(value = "SELECT DISTINCT DATE(timestamp) FROM nifty_five_min_candle_data WHERE timestamp < :date "
			+ "ORDER BY DATE(timestamp) DESC " + "LIMIT :n", nativeQuery = true)
	List<java.sql.Date> findLastNDatesBefore(@Param("date") LocalDate date, @Param("n") int n);
	
	
	 @Query("SELECT c.timestamp FROM Nifty5MinCandle c WHERE c.timestamp BETWEEN :start AND :end")
	 List<LocalDateTime> findAllTimestampsBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
	 
	 
	 // 2. Find all candles after a given timestamp (ascending order)
	    List<Nifty5MinCandle> findByTimestampAfterOrderByTimestampAsc(LocalDateTime timestamp);
}
