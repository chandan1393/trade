package com.fyers.fyerstrading.model;

public class GTTModifiedOrderResponse {
    private int code;
    private String responseBody;

    public GTTModifiedOrderResponse() {}

    public GTTModifiedOrderResponse(int code, String responseBody) {
        this.code = code;
        this.responseBody = responseBody;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }

    @Override
    public String toString() {
        return "FyersResponse{" +
                "code=" + code +
                ", responseBody='" + responseBody + '\'' +
                '}';
    }
}

