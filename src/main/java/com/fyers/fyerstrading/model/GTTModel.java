package com.fyers.fyerstrading.model;

import java.util.HashMap;
import java.util.Map;

public class GTTModel {
    public int Side; // 1 for buy, -1 for sell
    public String Symbol;
    public String productType;
    public Map<String, GTTLeg> orderInfo = new HashMap<>();

    public void addGTTLeg(String leg, GTTLeg gttLeg) {
        orderInfo.put(leg, gttLeg);
    }
}
