package com.fyers.fyerstrading.model;

import java.util.List;

public class FyersOrderResponse {
    private int code;
    private String s;
    private String message;
    private List<Order> orderBook;

    // Getters and setters

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

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }

    public List<Order> getOrderBook() {
        return orderBook;
    }
    public void setOrderBook(List<Order> orderBook) {
        this.orderBook = orderBook;
    }
}

