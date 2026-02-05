package com.fyers.fyerstrading.entity;

import java.time.LocalDate;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "stock_daily_price", 
       uniqueConstraints = @UniqueConstraint(columnNames = { "stock_id", "trade_date" }))
public class StockDailyPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) 
    @JoinColumn(name = "stock_id", nullable = false)
    private StockMaster stock;

    @Column(name = "trade_date", nullable = false)
    private LocalDate tradeDate;

    @Column(nullable = false)
    private Double openPrice;

    @Column(nullable = false)
    private Double highPrice;

    @Column(nullable = false)
    private Double lowPrice;

    @Column(nullable = false)
    private Double closePrice;

    @Column(nullable = false)
    private Double volume;
    
    private Integer deliveryPercent;
    
    @OneToOne(mappedBy = "stockDailyPrice", cascade = CascadeType.ALL, orphanRemoval = true)
    private StockTechnicalIndicator technicalIndicator;


	public StockDailyPrice() {

	}

	public StockDailyPrice(StockMaster stock, LocalDate tradeDate, Double openPrice, Double highPrice, Double lowPrice,
			Double closePrice, Double volume) {
		super();
		this.stock = stock;
		this.tradeDate = tradeDate;
		this.openPrice = openPrice;
		this.highPrice = highPrice;
		this.lowPrice = lowPrice;
		this.closePrice = closePrice;
		this.volume = volume;
	}

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

	public LocalDate getTradeDate() {
		return tradeDate;
	}

	public void setTradeDate(LocalDate tradeDate) {
		this.tradeDate = tradeDate;
	}

	public Double getOpenPrice() {
		return openPrice;
	}

	public void setOpenPrice(Double openPrice) {
		this.openPrice = openPrice;
	}

	public Double getHighPrice() {
		return highPrice;
	}

	public void setHighPrice(Double highPrice) {
		this.highPrice = highPrice;
	}

	public Double getLowPrice() {
		return lowPrice;
	}

	public void setLowPrice(Double lowPrice) {
		this.lowPrice = lowPrice;
	}

	public Double getClosePrice() {
		return closePrice;
	}

	public void setClosePrice(Double closePrice) {
		this.closePrice = closePrice;
	}

	public Double getVolume() {
		return volume;
	}

	public void setVolume(Double volume) {
		this.volume = volume;
	}
	
	
	
	

	public Integer getDeliveryPercent() {
		return deliveryPercent;
	}

	public void setDeliveryPercent(Integer deliveryPercent) {
		this.deliveryPercent = deliveryPercent;
	}

	public StockTechnicalIndicator getTechnicalIndicator() {
		return technicalIndicator;
	}

	public void setTechnicalIndicator(StockTechnicalIndicator technicalIndicator) {
		this.technicalIndicator = technicalIndicator;
	}
	
	
	

}