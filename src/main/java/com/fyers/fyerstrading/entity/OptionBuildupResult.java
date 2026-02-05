package com.fyers.fyerstrading.entity;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "option_buildup_result")
public class OptionBuildupResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id", nullable = false)
    private StockMaster stock;

    private LocalDateTime timestamp;
    private double strikePrice;
    private String optionType; // "CE" or "PE"
    private long openInterest;
    private long oiChange;
    private double price;
    private String buildupType; // e.g., LONG_BUILDUP, SHORT_COVERING
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public StockMaster getStock() {
		return stock;
	}
	public void setStock(StockMaster stock) {
		this.stock = stock;
	}
	public LocalDateTime getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}
	public double getStrikePrice() {
		return strikePrice;
	}
	public void setStrikePrice(double strikePrice) {
		this.strikePrice = strikePrice;
	}
	public String getOptionType() {
		return optionType;
	}
	public void setOptionType(String optionType) {
		this.optionType = optionType;
	}
	public long getOpenInterest() {
		return openInterest;
	}
	public void setOpenInterest(long openInterest) {
		this.openInterest = openInterest;
	}
	public long getOiChange() {
		return oiChange;
	}
	public void setOiChange(long oiChange) {
		this.oiChange = oiChange;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public String getBuildupType() {
		return buildupType;
	}
	public void setBuildupType(String buildupType) {
		this.buildupType = buildupType;
	}
    
    
    
    
}

