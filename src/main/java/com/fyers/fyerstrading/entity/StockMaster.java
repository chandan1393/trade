package com.fyers.fyerstrading.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "stocks_master", uniqueConstraints = @UniqueConstraint(columnNames = {"symbol", "isin_code"}))
public class StockMaster{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="symbol",nullable = false, unique = true)
    private String symbol;

    @Column(nullable = false)
    private String companyName;

    private String exchange;

    @Column(name="isin_code",nullable = false, unique = true)
    private String isinCode;

    private String series;  // EQ, BE, etc.

    private LocalDateTime addedDate = LocalDateTime.now();

    @ManyToMany(mappedBy = "stocks")
    private Set<IndexCategory> indices = new HashSet<>();
    
    @Column(name = "is_in_fno", nullable = false)
    private boolean isInFno = false;

    

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

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getExchange() {
		return exchange;
	}

	public void setExchange(String exchange) {
		this.exchange = exchange;
	}

	public String getIsinCode() {
		return isinCode;
	}

	public void setIsinCode(String isinCode) {
		this.isinCode = isinCode;
	}

	public String getSeries() {
		return series;
	}

	public void setSeries(String series) {
		this.series = series;
	}

	public LocalDateTime getAddedDate() {
		return addedDate;
	}

	public void setAddedDate(LocalDateTime addedDate) {
		this.addedDate = addedDate;
	}

	public Set<IndexCategory> getIndices() {
		return indices;
	}

	public void setIndices(Set<IndexCategory> indices) {
		this.indices = indices;
	}

	public boolean isInFno() {
		return isInFno;
	}

	public void setInFno(boolean isInFno) {
		this.isInFno = isInFno;
	}

    
    
    
    

}