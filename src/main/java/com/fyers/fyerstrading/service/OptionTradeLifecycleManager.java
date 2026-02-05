package com.fyers.fyerstrading.service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fyers.fyerstrading.dto.ExitPlanRequest;
import com.fyers.fyerstrading.entity.IndexOptionTradeLeg;
import com.fyers.fyerstrading.entity.OptionInstrument;
import com.fyers.fyerstrading.enu.InstrumentType;
import com.fyers.fyerstrading.model.NetPosition;
import com.fyers.fyerstrading.model.OptionTrade;
import com.fyers.fyerstrading.model.PositionResponse;
import com.fyers.fyerstrading.model.StockOptionTradeLeg;
import com.fyers.fyerstrading.repo.IndexOptionTradeLegRepository;
import com.fyers.fyerstrading.repo.OptionInstrumentRepository;
import com.fyers.fyerstrading.repo.OptionTradeRepository;
import com.fyers.fyerstrading.repo.StockOptionTradeLegRepository;
import com.fyers.fyerstrading.websocket.MarketDataWebSocketService;

@Service
public class OptionTradeLifecycleManager {

	@Autowired
	private FyersApiService fyersApiService;

	@Autowired
	private OptionTradeRepository tradeRepo;

	@Autowired
	private StockOptionTradeLegRepository stockLegRepo;

	@Autowired
	private IndexOptionTradeLegRepository indexLegRepo;

	@Autowired
	private StockOptionLogicEngine stockLogicEngine;

	@Autowired
	private IndexOptionLogicEngine indexLogicEngine;

	@Autowired
	private OptionInstrumentRepository instrumentRepo;

	@Autowired
	private MarketDataWebSocketService marketDataWebSocketService;

	public void syncFromBroker() {

		PositionResponse resp = fyersApiService.getPositions();
		List<NetPosition> positions = resp.getNetPositions();

		Set<String> liveKeys = new HashSet<>();

		for (NetPosition p : positions) {

			String symbol = p.getSymbol();
			int qty = p.getNetQty();
			double entry = p.getBuyAvg();
			String side = p.getSide() == 1 ? "BUY" : "SELL";

			String key = symbol + ":" + side;
			liveKeys.add(key);

			OptionTrade trade = tradeRepo.findBySymbolAndSideAndStatus(symbol, side, "OPEN");

			if (trade == null && qty > 0) {
				createNewTradeFromPosition(p);
			} else if (trade != null) {
				updateTradeQuantity(trade, qty, entry);
			}
		}

		closeMissingTrades(liveKeys);
	}

	private void createNewTradeFromPosition(NetPosition p) {

		OptionTrade t = new OptionTrade();
		t.setSymbol(p.getSymbol());
		t.setSide(p.getSide() == 1 ? "BUY" : "SELL");
		t.setTotalQuantity(p.getNetQty());
		t.setEntryPrice(p.getBuyAvg());
		t.setStatus("OPEN");
		t.setCreatedAt(LocalDateTime.now());

		// Defaults
		t.setLotSize(1);
		t.setUnderlying("UNKNOWN");
		t.setInstrumentType(InstrumentType.STOCK_OPTION);

		OptionInstrument inst = instrumentRepo.findById(p.getSymbol()).orElse(null);
		if (inst != null) {
			t.setLotSize(inst.getLotSize());
			t.setUnderlying(inst.getUnderlying());
			t.setInstrumentType(resolveInstrumentType(inst.getUnderlying()));
			t.setExpiry(inst.getExpiry());
		}

		t.setStructureSL(0);
		t.setT1(0);
		t.setT2(0);
		t.setT3(0);

		tradeRepo.save(t);

		// 🔀 REGISTER IN CORRECT ENGINE
		if (t.getInstrumentType() == InstrumentType.STOCK_OPTION) {
			stockLogicEngine.registerTrade(t);
		} else {
			indexLogicEngine.registerTrade(t);
		}
	}

	private void updateTradeQuantity(OptionTrade trade, int qty, double entry) {

		if (qty == 0) {
			completeExitProcess(trade);
			return;
		}

		trade.setTotalQuantity(qty);
		trade.setEntryPrice(entry);
		tradeRepo.save(trade);
	}

	private void completeExitProcess(OptionTrade trade) {

		// 1️⃣ Close trade
		trade.setStatus("CLOSED");
		trade.setClosedAt(LocalDateTime.now());
		tradeRepo.save(trade);

		// 2️⃣ Exit legs
		if (trade.getInstrumentType() == InstrumentType.STOCK_OPTION) {

			List<StockOptionTradeLeg> legs = stockLegRepo.findByTradeId(trade.getTradeId());

			for (StockOptionTradeLeg leg : legs) {
				if (!"EXITED".equals(leg.getStatus())) {
					leg.setStatus("EXITED");
					leg.setExitedAt(LocalDateTime.now());
					stockLegRepo.save(leg);
				}
			}

			stockLogicEngine.unregisterTrade(trade.getTradeId());

		} else {

			List<IndexOptionTradeLeg> legs = indexLegRepo.findByTradeId(trade.getTradeId());

			for (IndexOptionTradeLeg leg : legs) {
				if (!"EXITED".equals(leg.getStatus())) {
					leg.setStatus("EXITED");
					leg.setExitedAt(LocalDateTime.now());
					indexLegRepo.save(leg);
				}
			}

			indexLogicEngine.unregisterTrade(trade.getTradeId());
		}

		// 3️⃣ WebSocket unsubscribe
		maybeUnsubscribe(trade.getSymbol());

		System.out.println("Trade fully closed & saved: " + trade.getSymbol());
	}

	public void maybeUnsubscribe(String symbol) {

		boolean stockActive = stockLogicEngine.getTradeIdsBySymbol(symbol) != null
				&& !stockLogicEngine.getTradeIdsBySymbol(symbol).isEmpty();

		boolean indexActive = indexLogicEngine.getTradeIdsBySymbol(symbol) != null
				&& !indexLogicEngine.getTradeIdsBySymbol(symbol).isEmpty();

		if (!stockActive && !indexActive) {
			marketDataWebSocketService.unsubscribeSymbol(symbol);
		}
	}

	private void closeMissingTrades(Set<String> liveKeys) {

		List<OptionTrade> openTrades = tradeRepo.findByStatus("OPEN");

		for (OptionTrade t : openTrades) {
			String key = t.getSymbol() + ":" + t.getSide();
			if (!liveKeys.contains(key)) {
				completeExitProcess(t);
			}
		}
	}

	private int resolveLotSize(String symbol) {
		return instrumentRepo.findById(symbol).map(OptionInstrument::getLotSize)
				.orElseThrow(() -> new RuntimeException("Lot size not found for " + symbol));
	}

	private InstrumentType resolveInstrumentType(String underlying) {
		Set<String> indices = Set.of("NIFTY", "BANKNIFTY", "FINNIFTY", "MIDCPNIFTY", "SENSEX");
		return indices.contains(underlying) ? InstrumentType.INDEX_OPTION : InstrumentType.STOCK_OPTION;
	}

	public List<OptionTrade> getAllOpenTrades() {
		return tradeRepo.findByStatusOrderByCreatedAtDesc("OPEN");
	}

	public List<OptionTrade> getAllOpenTradesWithoutTarget() {
		return tradeRepo.findByStatusAndT1OrderByCreatedAtDesc("OPEN", 0);
	}

	public OptionTrade updateExitPlan(Long tradeId, ExitPlanRequest req) {

		OptionTrade trade = tradeRepo.findById(tradeId).orElseThrow(() -> new RuntimeException("Trade not found"));

		trade.setStructureSL(req.getStructureSL());
		trade.setT1(req.getT1());
		trade.setT2(req.getT2());
		trade.setT3(req.getT3());

		if (req.getExpiry() != null) {
			trade.setExpiry(req.getExpiry());
		}

		tradeRepo.save(trade);

		// 🔀 ROUTE TO CORRECT ENGINE
		if (trade.getInstrumentType() == InstrumentType.STOCK_OPTION) {

			stockLogicEngine.registerTrade(trade);

			if (trade.getStructureSL() > 0 || trade.getT1() > 0) {
				stockLogicEngine.startForTrade(tradeId);
			}

		} else {

			indexLogicEngine.registerTrade(trade);

			if (trade.getT1() > 0) {
				indexLogicEngine.startForTrade(tradeId);
			}
		}

		marketDataWebSocketService.subscribeNewSymbol(trade.getSymbol());

		return trade;
	}

}
