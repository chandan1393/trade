package com.fyers.fyerstrading.repo;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fyers.fyerstrading.entity.StockEmaCrossover;


@Repository
public interface StockEmaCrossoverRepository extends JpaRepository<StockEmaCrossover, Long> {

	@Query("SELECT s FROM StockEmaCrossover s WHERE s.currentEma9 > s.currentEma21 AND s.tradeDate >= :sinceDate ORDER BY s.tradeDate DESC")
    List<StockEmaCrossover> findRecentBullishCrossovers(@Param("sinceDate") LocalDate sinceDate);


}

