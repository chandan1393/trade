package com.fyers.fyerstrading.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "index_categories", uniqueConstraints = @UniqueConstraint(columnNames = {"symbol"}))
public class IndexCategory{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String symbol;  // Example: "NIFTY50", "NIFTYBANK", "NIFTYMIDCAP"

    @Column(nullable = false)
    private String indexName;  // Example: "NIFTY 50", "NIFTY BANK"

    private String indexType;  // Example: "Broad Market", "Sectoral", "Thematic"

    private String description;

    @ManyToMany
    @JoinTable(
        name = "index_stock_mapping",
        joinColumns = @JoinColumn(name = "index_id"),
        inverseJoinColumns = @JoinColumn(name = "stock_id")
    )
    private Set<StockMaster> stocks = new HashSet<>();

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

	public String getIndexName() {
		return indexName;
	}

	public void setIndexName(String indexName) {
		this.indexName = indexName;
	}

	public String getIndexType() {
		return indexType;
	}

	public void setIndexType(String indexType) {
		this.indexType = indexType;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Set<StockMaster> getStocks() {
		return stocks;
	}

	public void setStocks(Set<StockMaster> stocks) {
		this.stocks = stocks;
	}
    
    
    
    

}
