package com.fyers.fyerstrading.entity;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

@Entity
@Table(name = "option_instrument", indexes = {
		@Index(name = "idx_underlying_expiry", columnList = "underlying, expiry"),
		@Index(name = "idx_underlying_strike_type", columnList = "underlying, strike, optionType") })
public class OptionInstrument {

	@Id
	private String symbol;

	private String underlying;
	private LocalDate expiry;
	private int strike;
	private String optionType; // CE / PE
	private int lotSize;
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public String getUnderlying() {
		return underlying;
	}
	public void setUnderlying(String underlying) {
		this.underlying = underlying;
	}
	public LocalDate getExpiry() {
		return expiry;
	}
	public void setExpiry(LocalDate expiry) {
		this.expiry = expiry;
	}
	public int getStrike() {
		return strike;
	}
	public void setStrike(int strike) {
		this.strike = strike;
	}
	public String getOptionType() {
		return optionType;
	}
	public void setOptionType(String optionType) {
		this.optionType = optionType;
	}
	public int getLotSize() {
		return lotSize;
	}
	public void setLotSize(int lotSize) {
		this.lotSize = lotSize;
	}

	
}
