package com.fyers.fyerstrading.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "strategy")
public class StrategyProperties {

	private String higherTimeframe = "15m";
	private int higherCount = 200;
	private String entryTimeframe = "5m";
	private int entryCount = 30;
	private int defaultQty = 1;

	// getters & setters

	public String getHigherTimeframe() {
		return higherTimeframe;
	}

	public void setHigherTimeframe(String higherTimeframe) {
		this.higherTimeframe = higherTimeframe;
	}

	public int getHigherCount() {
		return higherCount;
	}

	public void setHigherCount(int higherCount) {
		this.higherCount = higherCount;
	}

	public String getEntryTimeframe() {
		return entryTimeframe;
	}

	public void setEntryTimeframe(String entryTimeframe) {
		this.entryTimeframe = entryTimeframe;
	}

	public int getEntryCount() {
		return entryCount;
	}

	public void setEntryCount(int entryCount) {
		this.entryCount = entryCount;
	}

	public int getDefaultQty() {
		return defaultQty;
	}

	public void setDefaultQty(int defaultQty) {
		this.defaultQty = defaultQty;
	}
}