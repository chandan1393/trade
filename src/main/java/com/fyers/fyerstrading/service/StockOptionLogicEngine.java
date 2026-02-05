package com.fyers.fyerstrading.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fyers.fyerstrading.entity.IndexOptionTradeLeg;
import com.fyers.fyerstrading.enu.ExitType;
import com.fyers.fyerstrading.enu.InstrumentType;
import com.fyers.fyerstrading.enu.Side;
import com.fyers.fyerstrading.enu.TradeRole;
import com.fyers.fyerstrading.model.OptionTrade;
import com.fyers.fyerstrading.model.StockOptionTradeLeg;
import com.fyers.fyerstrading.repo.IndexOptionTradeLegRepository;
import com.fyers.fyerstrading.repo.OptionTradeRepository;
import com.fyers.fyerstrading.repo.StockOptionTradeLegRepository;

@Service
public class StockOptionLogicEngine {

	@Autowired
	private OptionTradeRepository tradeRepo;

	@Autowired
	private StockOptionTradeLegRepository stockLegRepo;

	@Autowired
	private OrderExecutionServiceForOption orderExecutionService;

	private final Map<Long, OptionTrade> tradeById = new HashMap<>();
	private final Map<Long, List<StockOptionTradeLeg>> legsByTrade = new ConcurrentHashMap<>();
	private final Map<String, List<Long>> tradeIdsBySymbol = new ConcurrentHashMap<>();

	private long legSeq = 1;

	// ================= STARTUP =================
	@PostConstruct
	public void loadFromDbOnStartup() {

		List<OptionTrade> trades = tradeRepo.findByStatusAndInstrumentType("OPEN", InstrumentType.STOCK_OPTION);

		for (OptionTrade t : trades) {
			registerTrade(t);
			if (hasExitPlan(t)) {
				startForTrade(t.getTradeId());
			}
		}

		System.out.println("Stock engine loaded trades: " + tradeById.size());
	}

	private boolean hasExitPlan(OptionTrade t) {
		return t.getStructureSL() > 0 || t.getT1() > 0 || t.getT2() > 0 || t.getT3() > 0;
	}

	// ================= REGISTER =================
	public void registerTrade(OptionTrade trade) {
		tradeById.put(trade.getTradeId(), trade);
		tradeIdsBySymbol.computeIfAbsent(trade.getSymbol(), k -> new CopyOnWriteArrayList<>()).add(trade.getTradeId());
	}

	public void unregisterTrade(Long tradeId) {
		OptionTrade t = tradeById.remove(tradeId);
		if (t == null)
			return;

		tradeIdsBySymbol.getOrDefault(t.getSymbol(), List.of()).remove(tradeId);
		legsByTrade.remove(tradeId);
	}

	// ================= START LOGIC =================
	public void startForTrade(Long tradeId) {

		OptionTrade t = tradeById.get(tradeId);
		if (t == null)
			return;

		List<StockOptionTradeLeg> legs = stockLegRepo.findByTradeId(tradeId);

		// ✅ CASE 1: Legs already exist → UPDATE THEM
		if (!legs.isEmpty()) {

			for (StockOptionTradeLeg leg : legs) {

				if (!"OPEN".equals(leg.getStatus()))
					continue;

				// 🔄 Always sync SL & targets from trade
				leg.setSl(t.getStructureSL());
				leg.setT1(t.getT1());
				leg.setT2(t.getT2());
				leg.setT3(t.getT3());
			}

			stockLegRepo.saveAll(legs);
			legsByTrade.put(tradeId, legs);

			System.out.println("Updated SL/targets for trade " + tradeId);
			return;
		}

		// ✅ CASE 2: No legs → create once
		createStockLegs(t);
	}

	// ================= LEG CREATION =================
	private void createStockLegs(OptionTrade t) {

		int totalQty = t.getTotalQuantity();
		int lotSize = t.getLotSize();

		if (lotSize <= 0 || totalQty <= 0)
			return;

		int lots = totalQty / lotSize;

		if (lots <= 0)
			return;

		int dte = dte(t.getExpiry());

		List<StockOptionTradeLeg> list = new ArrayList<>();

		// ================= CASE 1: ONLY 1 LOT =================
		if (lots == 1) {

			// Single runner leg — NO income leg
			list.add(buildLeg(t, 1, TradeRole.RUNNER, dte > 10 ? ExitType.SL_UPGRADE : ExitType.TRAIL, null, totalQty));

		}
		// ================= CASE 2: MULTIPLE LOTS =================
		else {

			// 1️⃣ Income leg → 1 lot only
			list.add(buildLeg(t, 1, TradeRole.INCOME, ExitType.TARGET, t.getT1(), lotSize));

			// 2️⃣ Remaining lots → runners
			int remainingQty = totalQty - lotSize;
			int runnerLots = remainingQty / lotSize;

			for (int i = 1; i <= runnerLots; i++) {
				list.add(buildLeg(t, i + 1, TradeRole.RUNNER, dte > 10 ? ExitType.SL_UPGRADE : ExitType.TRAIL, null,
						lotSize));
			}
		}

		stockLegRepo.saveAll(list);
		legsByTrade.put(t.getTradeId(), list);

		System.out.println("Stock legs created for trade " + t.getTradeId() + " | lots=" + lots);
	}

	private StockOptionTradeLeg buildLeg(OptionTrade t, int no, TradeRole role, ExitType type, Double target, int qty) {

		StockOptionTradeLeg l = new StockOptionTradeLeg();
		l.setLegId(legSeq++);
		l.setTradeId(t.getTradeId());
		l.setLegNo(no);
		l.setQuantity(qty);
		l.setRole(role);
		l.setExitType(type);
		l.setEntry(t.getEntryPrice());
		l.setSl(t.getStructureSL());
		l.setTarget(target);
		l.setT1(t.getT1());
		l.setT2(t.getT2());
		l.setT3(t.getT3());
		l.setStatus("OPEN");
		return l;
	}

	// ================= LIVE TICKS =================
	public void processLiveTick(String symbol, double ltp) {

		List<Long> tradeIds = tradeIdsBySymbol.get(symbol);
		if (tradeIds == null)
			return;

		for (Long tradeId : tradeIds) {
			handleTick(tradeId, ltp);
		}
	}

	private void handleTick(Long tradeId, double ltp) {

		List<StockOptionTradeLeg> legs = legsByTrade.get(tradeId);
		if (legs == null)
			return;

		OptionTrade trade = tradeById.get(tradeId);
		if (trade == null)
			return;

		int dte = dte(trade.getExpiry());

		for (StockOptionTradeLeg leg : legs) {

			if ("EXITED".equals(leg.getStatus()))
				continue;

			if (leg.getTarget() != null && ltp >= leg.getTarget()) {
				exitLeg(trade, leg);
				continue;
			}

			if (leg.getSl() > 0 && ltp <= leg.getSl()) {
				exitLeg(trade, leg);
				continue;
			}

			if (leg.getRole() != TradeRole.RUNNER)
				continue;

			if (!leg.isT1Hit() && ltp >= leg.getT1()) {
				upgradeSL(leg, leg.getEntry());
				leg.setT1Hit(true);
			}

			if (!leg.isT2Hit() && ltp >= leg.getT2()) {
				upgradeSL(leg, leg.getT1());
				leg.setT2Hit(true);
			}

			if (leg.isT3Hit()) {
				upgradeSL(leg, ltp * (1 - trailPct(dte)));
			}
		}
	}

	private void exitLeg(OptionTrade trade, StockOptionTradeLeg leg) {
		orderExecutionService.placeExitOrder(trade.getSymbol(), Side.SELL, leg.getQuantity());
		leg.setStatus("EXITED");
		stockLegRepo.save(leg);
	}

	private void upgradeSL(StockOptionTradeLeg leg, double newSL) {
		if (newSL > leg.getSl()) {
			leg.setSl(newSL);
			stockLegRepo.save(leg);
		}
	}

	private double trailPct(int dte) {
		if (dte > 15)
			return 0.25;
		if (dte > 10)
			return 0.22;
		if (dte > 7)
			return 0.20;
		if (dte > 5)
			return 0.18;
		return 0.15;
	}

	private int dte(LocalDate exp) {
		if (exp == null)
			return 0;
		return (int) ChronoUnit.DAYS.between(LocalDate.now(), exp);
	}

	public List<Long> getTradeIdsBySymbol(String symbol) {
		return tradeIdsBySymbol.get(symbol);
	}
}
