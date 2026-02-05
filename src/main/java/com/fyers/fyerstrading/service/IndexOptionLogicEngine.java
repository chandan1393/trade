package com.fyers.fyerstrading.service;

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
import com.fyers.fyerstrading.model.OptionTrade;
import com.fyers.fyerstrading.repo.IndexOptionTradeLegRepository;
import com.fyers.fyerstrading.repo.OptionTradeRepository;

@Service
public class IndexOptionLogicEngine {

	@Autowired
	private OptionTradeRepository tradeRepo;

	@Autowired
	private IndexOptionTradeLegRepository indexLegRepo;

	@Autowired
	private OrderExecutionServiceForOption orderExecutionService;

	private final Map<Long, OptionTrade> tradeById = new HashMap<>();
	private final Map<Long, List<IndexOptionTradeLeg>> legsByTrade = new ConcurrentHashMap<>();
	private final Map<String, List<Long>> tradeIdsBySymbol = new ConcurrentHashMap<>();

	@PostConstruct
	public void loadFromDbOnStartup() {

		List<OptionTrade> trades = tradeRepo.findByStatusAndInstrumentType("OPEN", InstrumentType.INDEX_OPTION);

		for (OptionTrade t : trades) {
			registerTrade(t);
			if (t.getT1() > 0) {
				startForTrade(t.getTradeId());
			}
		}

		System.out.println("Index engine loaded trades: " + tradeById.size());
	}

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

	public void startForTrade(Long tradeId) {

		OptionTrade t = tradeById.get(tradeId);
		if (t == null || t.getT1() <= 0)
			return;

		List<IndexOptionTradeLeg> existing = indexLegRepo.findByTradeId(tradeId);

		if (!existing.isEmpty()) {
			updateExistingLegs(t, existing);
			legsByTrade.put(tradeId, existing);
			return;
		}

		createIndexLegs(t);
	}

	private void createIndexLegs(OptionTrade t) {

		int qty = t.getTotalQuantity();
		int lotSize = t.getLotSize();
		int lots = qty / lotSize;

		List<IndexOptionTradeLeg> list = new ArrayList<>();

		if (lots == 1) {
			list.add(buildLeg(t, 1, qty, t.getT1()));
		} else if (lots < 4) {
			list.add(buildLeg(t, 1, lotSize, t.getT1()));
			list.add(buildLeg(t, 2, qty - lotSize, t.getT2()));
		} else {
			int q1 = qty * 50 / 100;
			int q2 = qty * 30 / 100;
			int q3 = qty - q1 - q2;
			list.add(buildLeg(t, 1, q1, t.getT1()));
			list.add(buildLeg(t, 2, q2, t.getT2()));
			list.add(buildLeg(t, 3, q3, t.getT3()));
		}

		indexLegRepo.saveAll(list);
		legsByTrade.put(t.getTradeId(), list);
	}

	private void updateExistingLegs(OptionTrade t, List<IndexOptionTradeLeg> legs) {

		for (IndexOptionTradeLeg leg : legs) {

			if ("EXITED".equals(leg.getStatus()))
				continue; // never touch exited legs

			if (leg.getLegNo() == 1) {
				leg.setTarget(t.getT1());
			} else if (leg.getLegNo() == 2) {
				leg.setTarget(t.getT2());
			} else if (leg.getLegNo() == 3) {
				leg.setTarget(t.getT3());
			}

			leg.setSl(t.getStructureSL());
		}

		indexLegRepo.saveAll(legs);
	}

	private IndexOptionTradeLeg buildLeg(OptionTrade t, int no, int qty, double target) {

		IndexOptionTradeLeg leg = new IndexOptionTradeLeg();
		leg.setTradeId(t.getTradeId());
		leg.setLegNo(no);
		leg.setQuantity(qty);
		leg.setEntry(t.getEntryPrice());
		leg.setTarget(target);
		leg.setSl(t.getStructureSL());
		leg.setExitType(ExitType.TARGET);
		leg.setStatus("OPEN");
		return leg;
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

		List<IndexOptionTradeLeg> legs = legsByTrade.get(tradeId);
		if (legs == null)
			return;

		for (IndexOptionTradeLeg leg : legs) {

			if (!"OPEN".equals(leg.getStatus()))
				continue;

			if (ltp >= leg.getTarget() || (leg.getSl() > 0 && ltp <= leg.getSl())) {

				orderExecutionService.placeExitOrder(tradeById.get(tradeId).getSymbol(), Side.SELL, leg.getQuantity());

				leg.setStatus("EXITED");
				indexLegRepo.save(leg);
			}
		}
	}

	public List<Long> getTradeIdsBySymbol(String symbol) {
		return tradeIdsBySymbol.get(symbol);
	}

}
