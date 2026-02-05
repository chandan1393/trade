package com.fyers.fyerstrading.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class PositionResponse {
	private int code;
	private String s;

	@JsonProperty("netPositions")
	private List<NetPosition> netPositions;

	private Overall overall;
	private String message;

	// getters & setters
	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getS() {
		return s;
	}

	public void setS(String s) {
		this.s = s;
	}

	public List<NetPosition> getNetPositions() {
		return netPositions;
	}

	public void setNetPositions(List<NetPosition> netPositions) {
		this.netPositions = netPositions;
	}

	public Overall getOverall() {
		return overall;
	}

	public void setOverall(Overall overall) {
		this.overall = overall;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
