package com.fyers.fyerstrading.config;

import org.springframework.stereotype.Component;

import com.fyers.fyerstrading.entity.StockDailyPrice;
import com.fyers.fyerstrading.model.MasterCandle;

@Component
public class StockDailyMapper {

    public MasterCandle toMasterCandle(StockDailyPrice e) {
        MasterCandle c = new MasterCandle();
        c.setId(e.getId());
        c.setSymbol("");
        c.setOpen(e.getOpenPrice());
        c.setHigh(e.getHighPrice());
        c.setLow(e.getLowPrice());
        c.setClose(e.getClosePrice());
        c.setVolume(e.getVolume());
        c.setTime(e.getTradeDate().atStartOfDay());  // converting LocalDate → LocalDateTime
        return c;
    }
}

