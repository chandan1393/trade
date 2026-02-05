package com.fyers.fyerstrading.model;



public class Topic {
	
	private String id;
	private String topicName;
	private String description;
	private String source;
	private String info;
	
	
	// Constructor, getters, and setters
    public Topic(String id, String topicName, String description, String source, String info) {
        this.id = id;
        this.topicName = topicName;
        this.description = description;
        this.source = source;
        this.info = info;
    }
    
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTopicName() {
		return topicName;
	}
	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getInfo() {
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
	}
	
	
	
	
	
	
}
