package com.fyers.fyerstrading.entity;

public enum TradeStatus {

	SETUP_FOUND, // Setup identified, waiting for entry
	PENDING_ENTRY, // Entry not yet executed but setup is valid
	ENTERED, OCO_ORDER_PLACED_FOR_T1, // Trade has been entered
	TARGET1_HIT, // Trade exited at the target price
	GTT_ORDER_MODIFIED_AFTER_T1,
	TARGET2_HIT, STOP_LOSS, // Trade hit stop-loss
	TIME_EXIT, // Trade exited due to time-based stop (max holding period)
	MANUAL_EXIT, // Trade exited manually (user intervention)
	CANCELLED, CANCELLED_STOP_GT_ENTRY, FAILED, REJECTED, ERROR_TO_PLACE_ORDER, TSL_AFTER_TARGET1, TSL_BEFORE_TARGET1,
	MANUAL_INTERVENTION_REQUIRED, CLOSED,GTT_SELL_ORDERS_PLACED

}
