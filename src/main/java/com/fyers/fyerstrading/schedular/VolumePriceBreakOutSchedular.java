package com.fyers.fyerstrading.schedular;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fyers.fyerstrading.service.VolumePriceBreakOutService;
import com.fyers.fyerstrading.service.swing.priceVolume.TradeManagerService;

@Component
public class VolumePriceBreakOutSchedular {


	@Autowired
	TradeManagerService tradeManagerService;

	
	@Autowired
	private VolumePriceBreakOutService vpBreakOutService;


	//@Scheduled(cron = "0 0 18-23/2 * * MON-FRI")
	public void findTradeSetups() {
		
		String schedulerName = "findTradeSetups";
		try {
			vpBreakOutService.findTradeSetupsForVolumePrice();
	    } catch (Exception e) {
	        System.err.println("❌ Exception in scheduler [" + schedulerName + "]: " + e.getMessage());
	        e.printStackTrace();
	       
	    }
	}

	//@Scheduled(cron = "0 0 17-23/2 * * MON-FRI")
	public void recalculateEntriesForUnexecutedTrades() {
		
		String schedulerName = "recalculateEntriesForUnexecutedTrades";
		try {
			vpBreakOutService.recalculateEntriesForUnexecutedTrades();
	    } catch (Exception e) {
	        System.err.println("❌ Exception in scheduler [" + schedulerName + "]: " + e.getMessage());
	        e.printStackTrace();
	       
	    }
	}

	//@Scheduled(cron = "0 0 17-23/2 * * MON-FRI")
	public void recalculateTrailingStopLossForExecutedTrades() {
		
		String schedulerName = "recalculateTrailingStopLossForExecutedTrades";
		try {
			vpBreakOutService.recalculateTrailingStopLossForExecutedTrades();
	    } catch (Exception e) {
	        System.err.println("❌ Exception in scheduler [" + schedulerName + "]: " + e.getMessage());
	        e.printStackTrace();
	       
	    }
	}
	
	//@Scheduled(cron = "0 0 */2 * * MON-FRI")
	public void modifyGTTSellOrdersForUpdatedTSL() {
		
		String schedulerName = "modifyGTTSellOrdersForUpdatedTSL";
		try {
			vpBreakOutService.modifyGTTSellOrdersForUpdatedTSL();
	    } catch (Exception e) {
	        System.err.println("❌ Exception in scheduler [" + schedulerName + "]: " + e.getMessage());
	        e.printStackTrace();
	       
	    }
	}
	
	
	//@Scheduled(cron = "0 0 */2 * * MON-FRI")
	public void gttSellOrderAfterEntry() {
		
		String schedulerName = "gttSellOrderAfterEntry";
		try {
			vpBreakOutService.placeGTTSellOrderAfterEntry(null);
	    } catch (Exception e) {
	        System.err.println("❌ Exception in scheduler [" + schedulerName + "]: " + e.getMessage());
	        e.printStackTrace();
	       
	    }
	}
	
	//@Scheduled(cron = "0 0 */2 * * MON-FRI")
	public void updatePositions() {
		
		String schedulerName = "updatePositions";
		try {
			vpBreakOutService.updatePositions();
	    } catch (Exception e) {
	        System.err.println("❌ Exception in scheduler [" + schedulerName + "]: " + e.getMessage());
	        e.printStackTrace();
	       
	    }
	}
	
	//@Scheduled(cron = "0 0 */2 * * MON-FRI")
	public void placeGTTOrderForAll() {
		String schedulerName = "placeGTTOrderForAll";
		try {
			vpBreakOutService.checkAndPlaceOrModifyAllEntryGTTOrders();
	    } catch (Exception e) {
	        System.err.println("❌ Exception in scheduler [" + schedulerName + "]: " + e.getMessage());
	        e.printStackTrace();
	       
	    }
		
		
	}
	

}
