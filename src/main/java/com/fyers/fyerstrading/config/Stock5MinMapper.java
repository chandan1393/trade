package com.fyers.fyerstrading.config;

import org.springframework.stereotype.Component;

import com.fyers.fyerstrading.entity.FNO5MinCandle;
import com.fyers.fyerstrading.model.MasterCandle;

@Component
public class Stock5MinMapper {

    public MasterCandle toMasterCandle(FNO5MinCandle e) {
        MasterCandle c = new MasterCandle();
        c.setSymbol(e.getSymbol());
        c.setOpen(e.getOpen());
        c.setHigh(e.getHigh());
        c.setLow(e.getLow());
        c.setClose(e.getClose());
        c.setVolume(e.getVolume());
        c.setTime(e.getTimestamp());   // already DateTime
        return c;
    }
}

