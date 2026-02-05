package com.fyers.fyerstrading.repo;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fyers.fyerstrading.entity.StockOIData;

public interface StockOIRepository extends JpaRepository<StockOIData, Long> {

	List<StockOIData> findBySymbolAndTimestamp(String symbol, LocalDateTime timestamp);

	@Query("SELECT s FROM StockOIData s " + "WHERE s.symbol = :symbol " + "AND s.timestamp = ("
			+ "   SELECT MAX(s2.timestamp) " + "   FROM StockOIData s2 " + "   WHERE s2.symbol = :symbol" + ")")
	List<StockOIData> findBySymbolAndLatest(@Param("symbol") String symbol);

	@Query("SELECT s FROM StockOIData s " + "WHERE s.symbol = :symbol " + "AND s.timestamp = ("
			+ "   SELECT MAX(s2.timestamp) " + "   FROM StockOIData s2 " + "   WHERE s2.symbol = :symbol "
			+ "   AND s2.timestamp < (" + "       SELECT MAX(s3.timestamp) " + "       FROM StockOIData s3 "
			+ "       WHERE s3.symbol = :symbol" + "   )" + ")")
	List<StockOIData> findPrevious(@Param("symbol") String symbol);

}
