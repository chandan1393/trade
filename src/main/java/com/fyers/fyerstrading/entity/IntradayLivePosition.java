package com.fyers.fyerstrading.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import com.fyers.fyerstrading.enu.Side;

@Entity
@Table(name = "intraday_live_position")
public class IntradayLivePosition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Side side;
    
    private String spotSymbol;
    private double spotEntryPrice;
    private double spotSl;
    private double spotBestPrice;
    private int qty;
    private boolean trailActive;
    private String brokerOrderId;

    private LocalDate tradeDate;
    private LocalDateTime entryTime;
 
    // OPTION
    private String optionSymbol;
    private double optionEntryPrice;
    private double optionExitPrice;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Side getSide() {
		return side;
	}
	public void setSide(Side side) {
		this.side = side;
	}

	public String getSpotSymbol() {
		return spotSymbol;
	}
	public void setSpotSymbol(String spotSymbol) {
		this.spotSymbol = spotSymbol;
	}
	public double getSpotEntryPrice() {
		return spotEntryPrice;
	}
	public void setSpotEntryPrice(double spotEntryPrice) {
		this.spotEntryPrice = spotEntryPrice;
	}
	public double getSpotSl() {
		return spotSl;
	}
	public void setSpotSl(double spotSl) {
		this.spotSl = spotSl;
	}
	public double getSpotBestPrice() {
		return spotBestPrice;
	}
	public void setSpotBestPrice(double spotBestPrice) {
		this.spotBestPrice = spotBestPrice;
	}
	public boolean isTrailActive() {
		return trailActive;
	}
	public void setTrailActive(boolean trailActive) {
		this.trailActive = trailActive;
	}
	public String getBrokerOrderId() {
		return brokerOrderId;
	}
	public void setBrokerOrderId(String brokerOrderId) {
		this.brokerOrderId = brokerOrderId;
	}
	public LocalDate getTradeDate() {
		return tradeDate;
	}
	public void setTradeDate(LocalDate tradeDate) {
		this.tradeDate = tradeDate;
	}
	public LocalDateTime getEntryTime() {
		return entryTime;
	}
	public void setEntryTime(LocalDateTime entryTime) {
		this.entryTime = entryTime;
	}
	public String getOptionSymbol() {
		return optionSymbol;
	}
	public void setOptionSymbol(String optionSymbol) {
		this.optionSymbol = optionSymbol;
	}
	public double getOptionEntryPrice() {
		return optionEntryPrice;
	}
	public void setOptionEntryPrice(double optionEntryPrice) {
		this.optionEntryPrice = optionEntryPrice;
	}
	public double getOptionExitPrice() {
		return optionExitPrice;
	}
	public void setOptionExitPrice(double optionExitPrice) {
		this.optionExitPrice = optionExitPrice;
	}
	public int getQty() {
		return qty;
	}
	public void setQty(int qty) {
		this.qty = qty;
	}

 


    
    
    
}
