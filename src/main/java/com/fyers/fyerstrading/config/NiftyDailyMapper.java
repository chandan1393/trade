package com.fyers.fyerstrading.config;

import org.springframework.stereotype.Component;

import com.fyers.fyerstrading.entity.NiftyDailyCandle;
import com.fyers.fyerstrading.model.MasterCandle;

@Component
public class NiftyDailyMapper {

    public MasterCandle toMasterCandle(NiftyDailyCandle e) {
        MasterCandle c = new MasterCandle();
        c.setSymbol("");
        c.setOpen(e.getOpen());
        c.setHigh(e.getHigh());
        c.setLow(e.getLow());
        c.setClose(e.getClose());
        c.setVolume(0);
        c.setTime(e.getTradeDate().atStartOfDay());   // already DateTime
        return c;
    }
}

