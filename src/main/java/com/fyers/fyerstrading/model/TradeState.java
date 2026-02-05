package com.fyers.fyerstrading.model;

import java.time.LocalDateTime;

import com.fyers.fyerstrading.enu.Side;

public class TradeState {

	double capital, riskPct;
	double cumPV = 0, cumVol = 0, vwap = 0;

	Side side;
	double entry, sl, tsl, riskPerShare, bestPrice;
	int qty;

	boolean tradeTaken = false;
	boolean exited = false;
	boolean trailActive = false;

	LocalDateTime setupTime, entryTime;

	TradeState(double capital, double riskPct) {
		this.capital = capital;
		this.riskPct = riskPct;
	}

}
