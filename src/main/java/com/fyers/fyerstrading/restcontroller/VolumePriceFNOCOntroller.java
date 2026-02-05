package com.fyers.fyerstrading.restcontroller;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fyers.fyerstrading.service.ImpulseConsolidationBreakoutFNOService;
import com.fyers.fyerstrading.service.VolumePriceBreakoutFNOService;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Volume Price BreakOut Controller FNO Management")
public class VolumePriceFNOCOntroller {

	  @Autowired
	    private ImpulseConsolidationBreakoutFNOService scannerService;
	  
	@Autowired
	VolumePriceBreakoutFNOService vpBreakOutService;
	
	@GetMapping("/findSetupsForVolumePriceBreakOutFNO")
	public ResponseEntity<String> swingTradeForVolumeBreakOutSetupsForDate() {
		vpBreakOutService.findTradeSetupsForVolumePrice();
		return ResponseEntity.ok("Completed");
	}
	
	
	
	@PostMapping("/scan-impulse-breakout/last-one-year")
	public void scanLastOneYear() {

		scannerService.scanAndSaveSetupsForLastOneYear();
	}
	
	
}
