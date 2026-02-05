package com.fyers.fyerstrading.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.fyers.fyerstrading.enu.InstrumentType;
import com.fyers.fyerstrading.model.OptionTrade;

public interface OptionTradeRepository extends JpaRepository<OptionTrade, Long>{

	OptionTrade findBySymbolAndSideAndStatus(String symbol, String side, String status);

	List<OptionTrade> findByStatus(String status);
	List<OptionTrade> findByStatusOrderByCreatedAtDesc(String status);
	List<OptionTrade> findByStatusAndT1OrderByCreatedAtDesc(String status, double t1);
	
	@Query("SELECT t FROM OptionTrade t WHERE t.status = 'OPEN'")
    List<OptionTrade> findAllOpen();

	long countByStatus(String string);

	List<OptionTrade> findByStatusAndInstrumentType(String string, InstrumentType indexOption);

}
