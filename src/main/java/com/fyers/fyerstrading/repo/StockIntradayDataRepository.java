package com.fyers.fyerstrading.repo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fyers.fyerstrading.entity.StockIntradayData;
import com.fyers.fyerstrading.entity.StockMaster;

public interface StockIntradayDataRepository extends JpaRepository<StockIntradayData, Long> {
	List<StockIntradayData> findByTimestampBetween(LocalDateTime start, LocalDateTime end);

	List<StockIntradayData> findByStockAndTimestampBetween(StockMaster stock, LocalDateTime from, LocalDateTime to);
	void deleteAllByTimestampBefore(LocalDateTime timestamp);
	void deleteByTimestampBefore(LocalDateTime cutoffDate);
	 List<StockIntradayData> findTop10ByStockOrderByTimestampDesc(StockMaster stock);

	Optional<StockIntradayData> findLatestByStock(Long id);

	List<StockIntradayData> findLatestByStock(StockMaster stock);
	@Query("SELECT d FROM StockIntradayData d WHERE d.stock = :stock ORDER BY d.timestamp DESC")
	List<StockIntradayData> findTopNByStockOrderByTimestampDesc(@Param("stock") StockMaster stock, PageRequest pageable);
	
	@Query("SELECT MAX(s.timestamp) FROM StockIntradayData s WHERE s.stock.id = :stockId")
	Optional<LocalDateTime> findLatestTimestampForStock(@Param("stockId") Long stockId);

}
