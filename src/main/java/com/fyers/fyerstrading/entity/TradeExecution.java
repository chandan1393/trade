package com.fyers.fyerstrading.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.stream.Stream;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

@Entity
@Table(name = "trade_execution")
public class TradeExecution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

 
    private String stockSymbol;
    private LocalDate orderPlacedDate;
    private LocalDateTime orderExecutedTime;
    private String gttExitOrderTSLId;
    private String gttExitOrderT1Id;
    private String gttExitOrderT2Id;

    // --- Entry Info ---
    private boolean entryExecuted;
    private LocalDate entryDate;
    private Double entryPrice;
    private Integer positionSize;

    // --- Exit Conditions ---
    private Double stopLoss;
    private Double target1;
    private Double target2;

    // --- Exit Tracking ---
    private boolean target1Hit;
    private boolean target2Hit;
    private boolean stopLossHit;

    private Integer exitedQtyTarget1;
    private Integer exitedQtyTarget2;
    private Integer exitedQtyTSL;

    private Double exitPriceTarget1;
    private Double exitPriceTarget2;
    private Double exitPriceTSL;

    private LocalDate exitDateTarget1;
    private LocalDate exitDateTarget2;
    private LocalDate exitDateTSL;

    // --- Summary ---
    private Integer totalExitedQty;
    private Double totalProfitLoss;

    private boolean fullyExited;
    private LocalDateTime syncTime;

    @Enumerated(EnumType.STRING)
    private TradeExitReason exitReason;

    @Enumerated(EnumType.STRING)
    private TradeStatus tradeStatus;

    private String remarks;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trade_setup_id", nullable = false)
    private TradeSetup tradeSetup;

    // --- Timestamps ---
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isClosed() {
        return this.tradeStatus == TradeStatus.CLOSED;
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getStockSymbol() {
		return stockSymbol;
	}

	public void setStockSymbol(String stockSymbol) {
		this.stockSymbol = stockSymbol;
	}

	public LocalDate getOrderPlacedDate() {
		return orderPlacedDate;
	}

	public void setOrderPlacedDate(LocalDate orderPlacedDate) {
		this.orderPlacedDate = orderPlacedDate;
	}

	public LocalDateTime getOrderExecutedTime() {
		return orderExecutedTime;
	}

	public void setOrderExecutedTime(LocalDateTime orderExecutedTime) {
		this.orderExecutedTime = orderExecutedTime;
	}

	

	public boolean isEntryExecuted() {
		return entryExecuted;
	}

	public void setEntryExecuted(boolean entryExecuted) {
		this.entryExecuted = entryExecuted;
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

	public Integer getPositionSize() {
		return positionSize;
	}

	public void setPositionSize(Integer positionSize) {
		this.positionSize = positionSize;
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

	public boolean isTarget1Hit() {
		return target1Hit;
	}

	public void setTarget1Hit(boolean target1Hit) {
		this.target1Hit = target1Hit;
	}

	public boolean isTarget2Hit() {
		return target2Hit;
	}

	public void setTarget2Hit(boolean target2Hit) {
		this.target2Hit = target2Hit;
	}

	public boolean isStopLossHit() {
		return stopLossHit;
	}

	public void setStopLossHit(boolean stopLossHit) {
		this.stopLossHit = stopLossHit;
	}

	public Integer getExitedQtyTarget1() {
		return exitedQtyTarget1;
	}

	public void setExitedQtyTarget1(Integer exitedQtyTarget1) {
		this.exitedQtyTarget1 = exitedQtyTarget1;
	}

	public Integer getExitedQtyTarget2() {
		return exitedQtyTarget2;
	}

	public void setExitedQtyTarget2(Integer exitedQtyTarget2) {
		this.exitedQtyTarget2 = exitedQtyTarget2;
	}

	
	public Integer getExitedQtyTSL() {
		return exitedQtyTSL;
	}

	public void setExitedQtyTSL(Integer exitedQtyTSL) {
		this.exitedQtyTSL = exitedQtyTSL;
	}

	public Double getExitPriceTarget1() {
		return exitPriceTarget1;
	}

	public void setExitPriceTarget1(Double exitPriceTarget1) {
		this.exitPriceTarget1 = exitPriceTarget1;
	}

	public Double getExitPriceTarget2() {
		return exitPriceTarget2;
	}

	public void setExitPriceTarget2(Double exitPriceTarget2) {
		this.exitPriceTarget2 = exitPriceTarget2;
	}

	

	public Double getExitPriceTSL() {
		return exitPriceTSL;
	}

	public void setExitPriceTSL(Double exitPriceTSL) {
		this.exitPriceTSL = exitPriceTSL;
	}

	public LocalDate getExitDateTarget1() {
		return exitDateTarget1;
	}

	public void setExitDateTarget1(LocalDate exitDateTarget1) {
		this.exitDateTarget1 = exitDateTarget1;
	}

	public LocalDate getExitDateTarget2() {
		return exitDateTarget2;
	}

	public void setExitDateTarget2(LocalDate exitDateTarget2) {
		this.exitDateTarget2 = exitDateTarget2;
	}

	

	public LocalDate getExitDateTSL() {
		return exitDateTSL;
	}

	public void setExitDateTSL(LocalDate exitDateTSL) {
		this.exitDateTSL = exitDateTSL;
	}

	public Double getTotalProfitLoss() {
		return totalProfitLoss;
	}

	public void setTotalProfitLoss(Double totalProfitLoss) {
		this.totalProfitLoss = totalProfitLoss;
	}

	public boolean isFullyExited() {
		return fullyExited;
	}

	public void setFullyExited(boolean fullyExited) {
		this.fullyExited = fullyExited;
	}

	public TradeExitReason getExitReason() {
		return exitReason;
	}

	public void setExitReason(TradeExitReason exitReason) {
		this.exitReason = exitReason;
	}

	public TradeStatus getTradeStatus() {
		return tradeStatus;
	}

	public void setTradeStatus(TradeStatus tradeStatus) {
		this.tradeStatus = tradeStatus;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public TradeSetup getTradeSetup() {
		return tradeSetup;
	}

	public void setTradeSetup(TradeSetup tradeSetup) {
		this.tradeSetup = tradeSetup;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	public void setTotalExitedQty(Integer totalExitedQty) {
		this.totalExitedQty = totalExitedQty;
	}


	public Integer getTotalExitedQty() {
		return totalExitedQty;
	}

	public String getGttExitOrderTSLId() {
		return gttExitOrderTSLId;
	}

	public void setGttExitOrderTSLId(String gttExitOrderTSLId) {
		this.gttExitOrderTSLId = gttExitOrderTSLId;
	}

	public String getGttExitOrderT1Id() {
		return gttExitOrderT1Id;
	}

	public void setGttExitOrderT1Id(String gttExitOrderT1Id) {
		this.gttExitOrderT1Id = gttExitOrderT1Id;
	}

	public String getGttExitOrderT2Id() {
		return gttExitOrderT2Id;
	}

	public void setGttExitOrderT2Id(String gttExitOrderT2Id) {
		this.gttExitOrderT2Id = gttExitOrderT2Id;
	}

	public LocalDateTime getSyncTime() {
		return syncTime;
	}

	public void setSyncTime(LocalDateTime syncTime) {
		this.syncTime = syncTime;
	}
    
    
    
    
    
    
}
