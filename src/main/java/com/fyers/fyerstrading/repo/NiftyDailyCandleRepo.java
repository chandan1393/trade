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

import com.fyers.fyerstrading.entity.NiftyDailyCandle;

public interface NiftyDailyCandleRepo extends JpaRepository<NiftyDailyCandle, Long> {

	
	@Query("SELECT c FROM NiftyDailyCandle c ORDER BY c.tradeDate DESC")
	List<NiftyDailyCandle> findTopNByOrderByTradeDateDesc(Pageable pageable);
	
	
	 default List<NiftyDailyCandle> findTopNByTradeDate(int n) {
	    	List<NiftyDailyCandle> list= findTopNByOrderByTradeDateDesc(PageRequest.of(0, n));
	         Collections.reverse(list);
	         return list;
	    }
	 
	 
	 
	 
	 
	@Query("SELECT MIN(n.tradeDate) FROM NiftyDailyCandle n")
	Optional<LocalDate> findOldestDate();
	
	@Query("SELECT MAX(n.tradeDate) FROM NiftyDailyCandle n")
	Optional<LocalDate> findLatestDate();
	
	@Query("SELECT n.tradeDate FROM NiftyDailyCandle n")
    Set<LocalDate> findExistingDates();
	
	@Query("SELECT n FROM NiftyDailyCandle n WHERE n.tradeDate = :tradeDate")
	NiftyDailyCandle findRecordOnDate(@Param("tradeDate") LocalDate tradeDate);
	
	@Query("SELECT n FROM NiftyDailyCandle n WHERE n.tradeDate < :tradeDate ORDER BY n.tradeDate DESC")
	List<NiftyDailyCandle> findRecentBeforeDate(@Param("tradeDate") LocalDate tradeDate, Pageable pageable);
	
	
	default List<NiftyDailyCandle> findRecordsBeforeDate(@Param("tradeDate") LocalDate tradeDate,int n){
		List<NiftyDailyCandle> list = findRecentBeforeDate(tradeDate,PageRequest.of(0, n));
		 Collections.reverse(list);
         return list;
	}
	
	@Query("SELECT n FROM NiftyDailyCandle n WHERE n.tradeDate >= :tradeDate ORDER BY n.tradeDate")
	List<NiftyDailyCandle> findAllAfterDate(@Param("tradeDate") LocalDate tradeDate);
	
	
	@Query("SELECT n FROM NiftyDailyCandle n WHERE n.tradeDate >= :startDate and n.tradeDate <= :endDate ORDER BY n.tradeDate")
	List<NiftyDailyCandle> findAllBWDate(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
	
	

	
	@Query("SELECT c FROM NiftyDailyCandle c where c.tradeDate< :tradeDate ORDER BY c.tradeDate DESC")
	List<NiftyDailyCandle> findTopNByOrderByTradeDateDesc(@Param("tradeDate") LocalDate tradeDate,Pageable pageable);

   


}
