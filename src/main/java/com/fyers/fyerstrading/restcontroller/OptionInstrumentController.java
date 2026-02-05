package com.fyers.fyerstrading.restcontroller;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.fyers.fyerstrading.service.OptionInstrumentUpdateService;

@Controller
public class OptionInstrumentController {

	@Autowired
	private OptionInstrumentUpdateService optionInstrumentUpdateService;
	
	
	@GetMapping("/optionInstrumentUpdate")
    public boolean optionInstrumentUpdate() {
		optionInstrumentUpdateService.refreshOptionInstruments(LocalDate.now());
         return true;
    }
	
	
	
}
