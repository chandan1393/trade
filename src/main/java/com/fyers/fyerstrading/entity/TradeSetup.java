package com.fyers.fyerstrading.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "trade_setup")
public class TradeSetup {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String stockSymbol;

	@Column(nullable = false)
	private LocalDate tradeFoundDate; 

	@Column(nullable = true)
	private LocalDate lastEvaluatedDate;

	@Column(nullable = true)
	private Double entryPrice; 

	@Column(nullable = true)
	private Double stopLoss;

	@Column(nullable = true)
	private Double target1;

	@Column(nullable = true)
	private Double target2;
	
	private Double initialHigh;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private TradeStatus tradeStatus = TradeStatus.SETUP_FOUND;

	@Column(nullable = true)
	private String strategyName;

	@Column(nullable = true)
	private Double riskRewardRatio;

	@Column(nullable = true)
	private Double capitalPlanned; // Capital that would be allocated if executed

	@Column(nullable = true)
	private String notes; // Optional notes, reasons for ranking, rejections, etc.

	@Column(nullable = false)
	private Boolean tradeEntered;

	@Column(nullable = false)
	private int positionSize;

	@Column(nullable = false)
	private Boolean isActive;
	
	@Column(nullable = true)
	private int tradeRank;
	
	@Column(nullable = true)
	private int deliveryPercent;
	
    private String gttEntryOrderId;  
    
    private LocalDateTime syncTime;


	
	@OneToOne(mappedBy = "tradeSetup", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private TradeExecution tradeExecution;

	public Long getId() {
		return id;
	}

	public String getStockSymbol() {
		return stockSymbol;
	}

	public void setStockSymbol(String stockSymbol) {
		this.stockSymbol = stockSymbol;
	}

	public LocalDate getTradeFoundDate() {
		return tradeFoundDate;
	}

	public void setTradeFoundDate(LocalDate tradeFoundDate) {
		this.tradeFoundDate = tradeFoundDate;
	}

	public LocalDate getLastEvaluatedDate() {
		return lastEvaluatedDate;
	}

	public void setLastEvaluatedDate(LocalDate lastEvaluatedDate) {
		this.lastEvaluatedDate = lastEvaluatedDate;
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


	public TradeStatus getTradeStatus() {
		return tradeStatus;
	}

	public void setTradeStatus(TradeStatus tradeStatus) {
		this.tradeStatus = tradeStatus;
	}

	public String getStrategyName() {
		return strategyName;
	}

	public void setStrategyName(String strategyName) {
		this.strategyName = strategyName;
	}

	public Double getRiskRewardRatio() {
		return riskRewardRatio;
	}

	public void setRiskRewardRatio(Double riskRewardRatio) {
		this.riskRewardRatio = riskRewardRatio;
	}

	public Double getCapitalPlanned() {
		return capitalPlanned;
	}

	public void setCapitalPlanned(Double capitalPlanned) {
		this.capitalPlanned = capitalPlanned;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public Boolean getTradeEntered() {
		return tradeEntered;
	}

	public void setTradeEntered(Boolean tradeEntered) {
		this.tradeEntered = tradeEntered;
	}

	public int getPositionSize() {
		return positionSize;
	}

	public void setPositionSize(int positionSize) {
		this.positionSize = positionSize;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public TradeExecution getTradeExecution() {
		return tradeExecution;
	}

	public void setTradeExecution(TradeExecution tradeExecution) {
		this.tradeExecution = tradeExecution;
	}

	public int getTradeRank() {
		return tradeRank;
	}

	public void setTradeRank(int tradeRank) {
		this.tradeRank = tradeRank;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getDeliveryPercent() {
		return deliveryPercent;
	}

	public void setDeliveryPercent(int deliveryPercent) {
		this.deliveryPercent = deliveryPercent;
	}

	
	public String getGttEntryOrderId() {
		return gttEntryOrderId;
	}

	public void setGttEntryOrderId(String gttEntryOrderId) {
		this.gttEntryOrderId = gttEntryOrderId;
	}

	public LocalDateTime getSyncTime() {
		return syncTime;
	}

	public void setSyncTime(LocalDateTime syncTime) {
		this.syncTime = syncTime;
	}

	public Double getInitialHigh() {
		return initialHigh;
	}

	public void setInitialHigh(Double initialHigh) {
		this.initialHigh = initialHigh;
	}

	
	
}
