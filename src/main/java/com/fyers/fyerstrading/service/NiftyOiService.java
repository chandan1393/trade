package com.fyers.fyerstrading.service;

import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fyers.fyerstrading.dto.OiSnapshot;
import com.fyers.fyerstrading.dto.OiStrikeRow;
import com.fyers.fyerstrading.dto.OiSummary;
import com.fyers.fyerstrading.entity.IndexOIData;
import com.fyers.fyerstrading.repo.IndexOIRepository;

@Service
public class NiftyOiService {

	@Autowired
	private IndexOIRepository repo;

	@Autowired
	private LivePriceStore livePriceStore;

	public OiSnapshot snapshot(int range) {

		double ltp = livePriceStore.getPrice("NSE:NIFTY50-INDEX");
		int atm = nearestStrike(ltp);

		List<IndexOIData> raw = repo.findLatest();

		// 🔹 Filter ATM ± range
		List<IndexOIData> filtered = raw.stream().filter(r -> Math.abs(r.getStrikePrice() - atm) <= range).toList();

		long totalCall = raw.stream().mapToLong(IndexOIData::getCallOI).sum();
		long totalPut = raw.stream().mapToLong(IndexOIData::getPutOI).sum();

		double pcr = totalPut * 1.0 / totalCall;

		double support = raw.stream().max(Comparator.comparingLong(IndexOIData::getPutOI)).map(IndexOIData::getStrikePrice)
				.orElse(0.0);

		double resistance = raw.stream().max(Comparator.comparingLong(IndexOIData::getCallOI))
				.map(IndexOIData::getStrikePrice).orElse(0.0);

		List<OiStrikeRow> rows = filtered.stream().map(r -> mapRow(r, ltp)).toList();

		OiSummary summary = new OiSummary();
		summary.setNiftyLtp(ltp);
		summary.setPcr(pcr);
		summary.setBias(bias(pcr));
		summary.setBiasStrength(biasStrength(pcr));

		OiSnapshot snap = new OiSnapshot();
		snap.setSummary(summary);
		snap.setSupport(support);
		snap.setResistance(resistance);
		snap.setStrikes(rows);

		return snap;
	}

	private OiStrikeRow mapRow(IndexOIData r, double ltp) {

		OiStrikeRow row = new OiStrikeRow();
		row.setStrike(r.getStrikePrice());
		row.setCallOi(r.getCallOI());
		row.setPutOi(r.getPutOI());
		row.setCallChgOi(r.getCallChgOI());
		row.setPutChgOi(r.getPutChgOI());

		// 🔥 Zone logic
		if (r.getPutOI() > r.getCallOI() * 1.5 && r.getPutChgOI() > 0)
			row.setZone("STRONG_SUPPORT");
		else if (r.getPutOI() > r.getCallOI())
			row.setZone("WEAK_SUPPORT");
		else if (r.getCallOI() > r.getPutOI() * 1.5 && r.getCallChgOI() > 0)
			row.setZone("STRONG_RESISTANCE");
		else if (r.getCallOI() > r.getPutOI())
			row.setZone("WEAK_RESISTANCE");
		else
			row.setZone("NEUTRAL");

		// 🔥 Trap logic
		if (r.getCallOI() > r.getPutOI() && r.getCallChgOI() < 0 && ltp > r.getStrikePrice())
			row.setTrap("CALL_TRAP");
		else if (r.getPutOI() > r.getCallOI() && r.getPutChgOI() < 0 && ltp < r.getStrikePrice())
			row.setTrap("PUT_TRAP");
		else
			row.setTrap("NONE");

		return row;
	}

	private String bias(double pcr) {
		if (pcr > 1.05)
			return "BULLISH";
		if (pcr < 0.95)
			return "BEARISH";
		return "RANGE";
	}

	private int biasStrength(double pcr) {
		return (int) (Math.min(1, Math.abs(pcr - 1)) * 100);
	}

	private int nearestStrike(double ltp) {
		return ((int) (ltp / 50)) * 50;
	}
}
