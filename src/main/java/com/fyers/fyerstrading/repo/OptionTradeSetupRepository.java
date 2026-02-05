package com.fyers.fyerstrading.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fyers.fyerstrading.entity.OptionTradeSetup;

@Repository
public interface OptionTradeSetupRepository extends JpaRepository<OptionTradeSetup, Long> {

    List<OptionTradeSetup> findByStatus(String status);

	boolean existsBySymbolAndStatus(String contract, String string);

}
