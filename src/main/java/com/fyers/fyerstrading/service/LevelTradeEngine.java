package com.fyers.fyerstrading.service;

import com.fyers.fyerstrading.entity.Nifty5MinCandle;
import com.fyers.fyerstrading.enu.TradeAction;

public class LevelTradeEngine {

	public TradeAction decideTrade(double support, double resistance, Nifty5MinCandle prev, Nifty5MinCandle current) {

		double price = current.getClose();

		// ---- Near Resistance ----
		if (price >= resistance - 10) {

			boolean breakout = current.getClose() > resistance && prev.getClose() > resistance;

			boolean fakeBreakout = prev.getClose() > resistance && current.getClose() < resistance;

			boolean rejection = current.getHigh() > resistance && current.getClose() < resistance
					&& current.getClose() < current.getClose();

			if (fakeBreakout)
				return TradeAction.SELL; // trap buyers
			if (rejection)
				return TradeAction.SELL;
			if (breakout)
				return TradeAction.BUY;
		}

		// ---- Near Support ----
		if (price <= support + 10) {

			boolean breakdown = current.getClose() < support && prev.getClose() < support;

			boolean fakeBreakdown = prev.getClose() < support && current.getClose() > support;

			boolean rejection = current.getLow() < support && current.getClose() > support
					&& current.getClose() > current.getOpen();

			if (fakeBreakdown)
				return TradeAction.BUY; // trap sellers
			if (rejection)
				return TradeAction.BUY;
			if (breakdown)
				return TradeAction.SELL;
		}

		return TradeAction.NO_TRADE;
	}

}
