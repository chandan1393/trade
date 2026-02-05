package com.fyers.fyerstrading.service;

import java.util.List;

import org.springframework.stereotype.Component;

import com.fyers.fyerstrading.entity.IndexOIData;
import com.fyers.fyerstrading.model.Level;

@Component
public class IndexOILevelDetector {

	public Level detectBaseOrResistance(List<IndexOIData> rows, double spotPrice) {

		IndexOIData bestPut = null;
		IndexOIData bestCall = null;

		for (IndexOIData r : rows) {

			// Support candidate: Put writing below or near spot
			if (r.getStrikePrice() <= spotPrice && r.getPutChgOI() != null && r.getPutLTP() != null
					&& r.getPutChgOI() > 0 && r.getPutLTP() < 0) {

				if (bestPut == null || r.getPutChgOI() > bestPut.getPutChgOI())
					bestPut = r;
			}

			// Resistance candidate: Call writing above or near spot
			if (r.getStrikePrice() >= spotPrice && r.getCallChgOI() != null && r.getCallLTP() != null
					&& r.getCallChgOI() > 0 && r.getCallLTP() < 0) {

				if (bestCall == null || r.getCallChgOI() > bestCall.getCallChgOI())
					bestCall = r;
			}
		}

		if (bestPut == null && bestCall == null)
			return null;

		Level lvl = new Level();

		if (bestPut != null && (bestCall == null || bestPut.getPutChgOI() > bestCall.getCallChgOI())) {
			lvl.strike = bestPut.getStrikePrice();
			lvl.type = "SUPPORT";
			lvl.strength = strength(bestPut.getPutChgOI().intValue());
		} else {
			lvl.strike = bestCall.getStrikePrice();
			lvl.type = "RESISTANCE";
			lvl.strength = strength(bestCall.getCallChgOI().intValue());
		}
		return lvl;
	}

	private String strength(Integer chgOi) {
		if (chgOi == null)
			return "WEAK";
		if (chgOi > 100000)
			return "VERY_STRONG";
		if (chgOi > 50000)
			return "STRONG";
		return "MODERATE";
	}

	// Check if detected level is breaking
	public boolean isLevelBreaking(IndexOIData row, String type) {

		if ("SUPPORT".equals(type)) {
			return row.getPutChgOI() != null && row.getPutLTP() != null
					&& (row.getPutChgOI() < 0 || row.getPutLTP() > 0);
		}

		if ("RESISTANCE".equals(type)) {
			return row.getCallChgOI() != null && row.getCallLTP() != null
					&& (row.getCallChgOI() < 0 || row.getCallLTP() > 0);
		}
		return false;
	}
}
