package com.fyers.fyerstrading.entity;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "top_movers_stocks")
public class TopMoversStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String symbol;

    @Column(nullable = false)
    private LocalDate tradeDate;

    @Column(nullable = false)
    private double deliveryPercent;

    @Column(nullable = false)
    private double percentMove;

    @Column(nullable = false)
    private double openPrice;

    @Column(nullable = false)
    private double closePrice;

    @Column(nullable = false)
    private boolean isFnO;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public LocalDate getTradeDate() {
		return tradeDate;
	}

	public void setTradeDate(LocalDate tradeDate) {
		this.tradeDate = tradeDate;
	}

	public double getDeliveryPercent() {
		return deliveryPercent;
	}

	public void setDeliveryPercent(double deliveryPercent) {
		this.deliveryPercent = deliveryPercent;
	}

	public double getPercentMove() {
		return percentMove;
	}

	public void setPercentMove(double percentMove) {
		this.percentMove = percentMove;
	}

	public double getOpenPrice() {
		return openPrice;
	}

	public void setOpenPrice(double openPrice) {
		this.openPrice = openPrice;
	}

	public double getClosePrice() {
		return closePrice;
	}

	public void setClosePrice(double closePrice) {
		this.closePrice = closePrice;
	}

	public boolean isFnO() {
		return isFnO;
	}

	public void setFnO(boolean isFnO) {
		this.isFnO = isFnO;
	}
    
    
    
}
