package com.fyers.fyerstrading.model;

import java.time.LocalDateTime;

public class TradeSignal {
	private String action; // BUY / SELL
	private String optionType; // CE / PE
	private String symbol; // Stock symbol
	private double strikePrice; // Option strike price
	private String expiryDate; // Expiry date of the contract
	private double ltp; // Last traded price
	private double oiChange; // Open Interest change
	private double oiChangePercent; // OI % Change
	private double volume; // Total volume
	private double iv; // Implied Volatility
	private double stopLoss; // Suggested Stop Loss
	private double targetPrice; // Suggested Target
	private LocalDateTime signalTime; // Timestamp when the signal was generated

	public TradeSignal(String action, String optionType, String symbol, double strikePrice, String expiryDate,
			double ltp, double oiChange, double oiChangePercent, double volume, double iv, double stopLoss,
			double targetPrice, LocalDateTime signalTime) {
		this.action = action;
		this.optionType = optionType;
		this.symbol = symbol;
		this.strikePrice = strikePrice;
		this.expiryDate = expiryDate;
		this.ltp = ltp;
		this.oiChange = oiChange;
		this.oiChangePercent = oiChangePercent;
		this.volume = volume;
		this.iv = iv;
		this.stopLoss = stopLoss;
		this.targetPrice = targetPrice;
		this.signalTime = signalTime;
	}

	// Getters
	public String getAction() {
		return action;
	}

	public String getOptionType() {
		return optionType;
	}

	public String getSymbol() {
		return symbol;
	}

	public double getStrikePrice() {
		return strikePrice;
	}

	public String getExpiryDate() {
		return expiryDate;
	}

	public double getLtp() {
		return ltp;
	}

	public double getOiChange() {
		return oiChange;
	}

	public double getOiChangePercent() {
		return oiChangePercent;
	}

	public double getVolume() {
		return volume;
	}

	public double getIv() {
		return iv;
	}

	public double getStopLoss() {
		return stopLoss;
	}

	public double getTargetPrice() {
		return targetPrice;
	}

	public LocalDateTime getSignalTime() {
		return signalTime;
	}
}
