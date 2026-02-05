package com.fyers.fyerstrading.config;

import org.springframework.stereotype.Component;

import com.fyers.fyerstrading.entity.Nifty5MinCandle;
import com.fyers.fyerstrading.model.MasterCandle;

@Component
public class Nifty5MinMapper {

    public MasterCandle toMasterCandle(Nifty5MinCandle e) {
        MasterCandle c = new MasterCandle();
        c.setSymbol("");
        c.setOpen(e.getOpen());
        c.setHigh(e.getHigh());
        c.setLow(e.getLow());
        c.setClose(e.getClose());
        c.setVolume(0);
        c.setTime(e.getTimestamp());   // already DateTime
        return c;
    }
}

