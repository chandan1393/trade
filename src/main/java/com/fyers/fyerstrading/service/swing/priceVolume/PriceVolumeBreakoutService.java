package com.fyers.fyerstrading.service.swing.priceVolume;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fyers.fyerstrading.entity.TradeExecution;
import com.fyers.fyerstrading.entity.TradeSetup;
import com.fyers.fyerstrading.repo.TradeExecutionRepository;
import com.fyers.fyerstrading.repo.TradeSetupRepository;

@Service
public class PriceVolumeBreakoutService {

	@Autowired
	TradeExecutionRepository tradeExecutionRepository;
	
	@Autowired
	TradeSetupRepository tradeSetupRepository;
	
	
	
	@Transactional
	public void deleteTradeSetupsByGttOrderIds(List<String> gttOrderIds) {
	    List<TradeExecution> executions = tradeSetupRepository.findByGttEntryOrderIdIn(gttOrderIds);

	    List<TradeSetup> setups = executions.stream()
	        .map(TradeExecution::getTradeSetup)
	        .filter(Objects::nonNull)
	        .collect(Collectors.toList());

	    tradeSetupRepository.deleteAll(setups);  // cascade + orphanRemoval will handle associated executions
	}
	
	
}
