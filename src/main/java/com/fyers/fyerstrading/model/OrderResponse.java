package com.fyers.fyerstrading.model;

public class OrderResponse {

	private int code;
    private String s;
    private String id;
    private String message;

    public OrderResponse() {}

    public OrderResponse(int code, String s, String id, String message) {
        this.code = code;
        this.s = s;
        this.id = id;
        this.message = message;
    }

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    @Override
    public String toString() {
        return "FyersResponse{" +
                "code=" + code +
                ", s='" + s + '\'' +
                ", id='" + id + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
