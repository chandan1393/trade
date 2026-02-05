package com.fyers.fyerstrading.entity;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "trade_signals")
public class TradeSignalEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String action;
    private String optionType;
    private String symbol;
    private double strikePrice;
    private String expiryDate;
    private double ltp;
    private double oiChange;
    private double oiChangePercent;
    private double volume;
    private double iv;
    private double stopLoss;
    private double targetPrice;
    private LocalDateTime signalTime;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getOptionType() {
		return optionType;
	}
	public void setOptionType(String optionType) {
		this.optionType = optionType;
	}
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public double getStrikePrice() {
		return strikePrice;
	}
	public void setStrikePrice(double strikePrice) {
		this.strikePrice = strikePrice;
	}
	public String getExpiryDate() {
		return expiryDate;
	}
	public void setExpiryDate(String expiryDate) {
		this.expiryDate = expiryDate;
	}
	public double getLtp() {
		return ltp;
	}
	public void setLtp(double ltp) {
		this.ltp = ltp;
	}
	public double getOiChange() {
		return oiChange;
	}
	public void setOiChange(double oiChange) {
		this.oiChange = oiChange;
	}
	public double getOiChangePercent() {
		return oiChangePercent;
	}
	public void setOiChangePercent(double oiChangePercent) {
		this.oiChangePercent = oiChangePercent;
	}
	public double getVolume() {
		return volume;
	}
	public void setVolume(double volume) {
		this.volume = volume;
	}
	public double getIv() {
		return iv;
	}
	public void setIv(double iv) {
		this.iv = iv;
	}
	public double getStopLoss() {
		return stopLoss;
	}
	public void setStopLoss(double stopLoss) {
		this.stopLoss = stopLoss;
	}
	public double getTargetPrice() {
		return targetPrice;
	}
	public void setTargetPrice(double targetPrice) {
		this.targetPrice = targetPrice;
	}
	public LocalDateTime getSignalTime() {
		return signalTime;
	}
	public void setSignalTime(LocalDateTime signalTime) {
		this.signalTime = signalTime;
	}




}
