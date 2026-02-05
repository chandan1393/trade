package com.fyers.fyerstrading.utility;

import com.fyers.fyerstrading.entity.WeeklyBreakOutBacktestResultEntity;
import com.fyers.fyerstrading.model.BacktestResult;

public class BacktestResultMapper {

	public static WeeklyBreakOutBacktestResultEntity toEntity(BacktestResult dto) {
	    WeeklyBreakOutBacktestResultEntity entity = new WeeklyBreakOutBacktestResultEntity();
	    entity.setSymbol(dto.getSymbol());
	    entity.setEntryDate(dto.getEntryDate());
	    entity.setEntry(dto.getEntry());
	    entity.setStopLoss(dto.getStopLoss());
	    entity.setTarget1(dto.getTarget1());
	    entity.setTarget2(dto.getTarget2());
	    entity.setExitReason(dto.getExitReason());
	    entity.setExitPrice(dto.getAverageExitPrice());
	    entity.setExitDate(dto.getExitDate());
	    entity.setPnlPercent(dto.getPnlPercent());
	    entity.setPnlAmount(dto.getProfitLoss());
	    entity.setBreakoutVolume(dto.getBreakoutVolume());
	    entity.setBreakoutCandleDate(dto.getBreakoutCandleDate());
	    entity.setBreakoutDeliveryPercent(dto.getBreakoutDeliveryPercent());
	    entity.setScore(dto.getScore());
	    entity.setAggressive(dto.isAggressive());

	    return entity;
	}

}

