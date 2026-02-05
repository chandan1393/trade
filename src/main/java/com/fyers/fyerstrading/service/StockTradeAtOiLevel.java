package com.fyers.fyerstrading.service;

import org.springframework.stereotype.Component;

import com.fyers.fyerstrading.entity.Nifty5MinCandle;
import com.fyers.fyerstrading.enu.TradeAction;

@Component
public class StockTradeAtOiLevel {

	public TradeAction tradeAtOiLevel(double support, double resistance, Nifty5MinCandle prev, Nifty5MinCandle curr) {

		// ----- At Support -----
		if (curr.getClose() <= support + 5) {

			boolean rejection = curr.getLow() < support && curr.getClose() > support
					&& curr.getClose() > curr.getOpen();

			boolean breakdown = curr.getClose() < support && prev.getClose() < support;

			boolean fakeBreakdown = prev.getClose() < support && curr.getClose() > support;

			if (fakeBreakdown || rejection)
				return TradeAction.BUY;
			if (breakdown)
				return TradeAction.SELL;
		}

		// ----- At Resistance -----
		if (curr.getClose() >= resistance - 5) {

			boolean rejection = curr.getHigh() > resistance && curr.getClose() < resistance
					&& curr.getClose() < curr.getOpen();

			boolean breakout = curr.getClose() > resistance && prev.getClose() > resistance;

			boolean fakeBreakout = prev.getClose() > resistance && curr.getClose() < resistance;

			if (fakeBreakout || rejection)
				return TradeAction.SELL;
			if (breakout)
				return TradeAction.BUY;
		}

		return TradeAction.NO_TRADE;
	}

}
