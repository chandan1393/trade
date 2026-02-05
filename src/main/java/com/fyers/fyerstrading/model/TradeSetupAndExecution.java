package com.fyers.fyerstrading.model;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fyers.fyerstrading.entity.TradeStatus;

@Entity
@Table(name = "trade_setup_execution")
public class TradeSetupAndExecution {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String GTTOrderId;
	private String stockSymbol;

	@Column(nullable = false)
	private LocalDate tradeFoundDate; // Date when the setup was found

	@Column(nullable = true)
	private LocalDate entryDate; // Date of entry (null if not entered)

	@Column(nullable = true)
	private Double entryPrice;

	@Column(nullable = true)
	private Double stopLoss;

	@Column(nullable = true)
	private Double trailingStopLoss; // Updated stop-loss after entry

	@Column(nullable = true)
	private Double target1;

	@Column(nullable = true)
	private LocalDate target1Date; // Date when target1 was hit

	@Column(nullable = true)
	private Double target2;

	@Column(nullable = true)
	private LocalDate target2Date; // Date when target2 was hit

	@Column(nullable = true)
	private Integer positionSize; // Number of shares/contracts

	@Column(nullable = true)
	private LocalDate exitDate; // Exit date

	@Column(nullable = true)
	private Double exitPrice; // Exit price

	@Column(nullable = true)
	private String exitReason; // E.g., hit SL, hit target, time-based exit

	@Column(nullable = true)
	private Double profitLoss; // Final P&L from trade

	@Column(nullable = false)
	private boolean tradeEntered = false; // Whether trade was entered

	@Column(nullable = false)
	private boolean partialExit = false; // If partial exit happened

	@Column(nullable = true)
	private Double riskRewardRatio; // (Target - Entry) / (Entry - SL)

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private TradeStatus tradeStatus = TradeStatus.SETUP_FOUND; // Status of the trade
	
	@Column(nullable = true)
	private String GTTOrderId1;
	
	@Column(nullable = true)
	private String GTTOrderId2;

	@Column(nullable = true)
	private Double capitalUsed; // Amount used for this trade

	@Column(nullable = true)
	private Double feesAndTaxes; // Any brokerage/taxes

	@Column(nullable = true)
	private String strategyName; // Name of the strategy used

	public TradeSetupAndExecution() {
		this.tradeEntered = false;
	}

	public TradeSetupAndExecution(String stockSymbol, LocalDate entryDate, Double entryPrice, Double stopLoss,
			Double target1, Double target2, Integer positionSize, boolean tradeExecuted, LocalDate tradeFoundDate) {
		this.stockSymbol = stockSymbol;
		this.entryDate = entryDate;
		this.entryPrice = entryPrice;
		this.stopLoss = stopLoss;
		this.target1 = target1;
		this.target2 = target2;
		this.positionSize = positionSize;
		this.tradeEntered = tradeExecuted;
		this.tradeFoundDate = tradeFoundDate;
	}

	public void setExitDetails(LocalDate exitDate, Double exitPrice, String exitReason, Double profitLoss) {
		this.exitDate = exitDate;
		this.exitPrice = exitPrice;
		this.exitReason = exitReason;
		this.profitLoss = profitLoss;
	}

	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Double getRiskRewardRatio() {
		return riskRewardRatio;
	}

	public void setRiskRewardRatio(Double riskRewardRatio) {
		this.riskRewardRatio = riskRewardRatio;
	}

	public TradeStatus getTradeStatus() {
		return tradeStatus;
	}

	public void setTradeStatus(TradeStatus tradeStatus) {
		this.tradeStatus = tradeStatus;
	}

	public Double getCapitalUsed() {
		return capitalUsed;
	}

	public void setCapitalUsed(Double capitalUsed) {
		this.capitalUsed = capitalUsed;
	}

	public Double getFeesAndTaxes() {
		return feesAndTaxes;
	}

	public void setFeesAndTaxes(Double feesAndTaxes) {
		this.feesAndTaxes = feesAndTaxes;
	}

	public String getStrategyName() {
		return strategyName;
	}

	public void setStrategyName(String strategyName) {
		this.strategyName = strategyName;
	}
	
	public String getStockSymbol() {
		return stockSymbol;
	}

	public void setStockSymbol(String stockSymbol) {
		this.stockSymbol = stockSymbol;
	}

	public LocalDate getEntryDate() {
		return entryDate;
	}

	public void setEntryDate(LocalDate entryDate) {
		this.entryDate = entryDate;
	}

	public Double getEntryPrice() {
		return entryPrice;
	}

	public void setEntryPrice(Double entryPrice) {
		this.entryPrice = entryPrice;
	}

	public Double getStopLoss() {
		return stopLoss;
	}

	public void setStopLoss(Double stopLoss) {
		this.stopLoss = stopLoss;
	}

	public Double getTarget1() {
		return target1;
	}

	public void setTarget1(Double target1) {
		this.target1 = target1;
	}

	public Double getTarget2() {
		return target2;
	}

	public void setTarget2(Double target2) {
		this.target2 = target2;
	}

	public boolean isPartialExit() {
		return partialExit;
	}

	public void setPartialExit(boolean partialExit) {
		this.partialExit = partialExit;
	}

	public Integer getPositionSize() {
		return positionSize;
	}

	public void setPositionSize(Integer positionSize) {
		this.positionSize = positionSize;
	}

	public LocalDate getExitDate() {
		return exitDate;
	}

	public void setExitDate(LocalDate exitDate) {
		this.exitDate = exitDate;
	}

	public Double getExitPrice() {
		return exitPrice;
	}

	public void setExitPrice(Double exitPrice) {
		this.exitPrice = exitPrice;
	}

	public String getExitReason() {
		return exitReason;
	}

	public void setExitReason(String exitReason) {
		this.exitReason = exitReason;
	}

	public boolean isTradeEntered() {
		return tradeEntered;
	}

	public void setTradeEntered(boolean tradeEntered) {
		this.tradeEntered = tradeEntered;
	}

	public Double getProfitLoss() {
		return profitLoss;
	}

	public void setProfitLoss(Double profitLoss) {
		this.profitLoss = profitLoss;
	}

	public LocalDate getTradeFoundDate() {
		return tradeFoundDate;
	}

	public void setTradeFoundDate(LocalDate tradeFoundDate) {
		this.tradeFoundDate = tradeFoundDate;
	}

	public Double getTrailingStopLoss() {
		return trailingStopLoss;
	}

	public void setTrailingStopLoss(Double trailingStopLoss) {
		this.trailingStopLoss = trailingStopLoss;
	}

	public LocalDate getTarget1Date() {
		return target1Date;
	}

	public void setTarget1Date(LocalDate target1Date) {
		this.target1Date = target1Date;
	}

	public LocalDate getTarget2Date() {
		return target2Date;
	}

	public void setTarget2Date(LocalDate target2Date) {
		this.target2Date = target2Date;
	}


	public String getGTTOrderId() {
		return GTTOrderId;
	}

	public void setGTTOrderId(String gTTOrderId) {
		GTTOrderId = gTTOrderId;
	}

	public String getGTTOrderId1() {
		return GTTOrderId1;
	}

	public void setGTTOrderId1(String gTTOrderId1) {
		GTTOrderId1 = gTTOrderId1;
	}

	public String getGTTOrderId2() {
		return GTTOrderId2;
	}

	public void setGTTOrderId2(String gTTOrderId2) {
		GTTOrderId2 = gTTOrderId2;
	}

	

	
	
	
}
