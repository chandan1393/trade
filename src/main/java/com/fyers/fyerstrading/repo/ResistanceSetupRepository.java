package com.fyers.fyerstrading.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fyers.fyerstrading.entity.ResistanceSetupEntity;

public interface ResistanceSetupRepository extends JpaRepository<ResistanceSetupEntity, Long> {

	ResistanceSetupEntity findBySymbol(String symbol);
}
