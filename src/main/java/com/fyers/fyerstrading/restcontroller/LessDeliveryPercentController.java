package com.fyers.fyerstrading.restcontroller;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fyers.fyerstrading.service.DayMoverWithLessDeliveryPercent;

@RestController
public class LessDeliveryPercentController {
	
	
	
	@Autowired
	DayMoverWithLessDeliveryPercent dayMoverWithLessDeliveryPercent;
	

	@GetMapping("/low-delivery-fno-stocks")
	public ResponseEntity<String> saveLowDeliveryFNOStocks() {

		dayMoverWithLessDeliveryPercent.findAndSaveRisingStocksWithLowDelivery();

		return ResponseEntity.ok("Low delivery stocks saved days.");
	}
	
	
	
	@GetMapping("/top-mover-stocks")
	public ResponseEntity<String> saveTopMoversStocks(@RequestParam("days") int days) {
		
		 LocalDate today = LocalDate.now();
		 LocalDate startDate = today.minusDays(days);

		    for (LocalDate date = startDate; !date.isAfter(today); date = date.plusDays(1)) {
		    	  dayMoverWithLessDeliveryPercent.findAndSaveTopMoversStocks(date);
		    	
		    }
		
		
	    return ResponseEntity.ok("Top Movers stocks saved for past " + days + " days.");
	}
}
