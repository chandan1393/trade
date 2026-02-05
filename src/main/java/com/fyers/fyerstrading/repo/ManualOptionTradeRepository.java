package com.fyers.fyerstrading.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fyers.fyerstrading.entity.ManualOptionTrade;

@Repository
public interface ManualOptionTradeRepository extends JpaRepository<ManualOptionTrade, Long> {
	Optional<ManualOptionTrade> findBySymbol(String symbol);

	ManualOptionTrade findBySymbolAndOpenTrue(String symbol);

	List<ManualOptionTrade> findAllByOpenTrue();

	List<ManualOptionTrade> findAllByOpenTrueOrderByEntryTimeDesc();
}
