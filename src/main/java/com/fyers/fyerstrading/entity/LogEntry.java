package com.fyers.fyerstrading.entity;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class LogEntry {
    @Id @GeneratedValue
    private Long id;

    private LocalDate logDate;
    private String path; // Google Drive link
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public LocalDate getLogDate() {
		return logDate;
	}
	public void setLogDate(LocalDate logDate) {
		this.logDate = logDate;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
    
    
    
    
}

