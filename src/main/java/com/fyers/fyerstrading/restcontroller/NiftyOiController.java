package com.fyers.fyerstrading.restcontroller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fyers.fyerstrading.dto.OiSnapshot;
import com.fyers.fyerstrading.service.NiftyOiService;

@RestController
@RequestMapping("/api/oi/nifty")
public class NiftyOiController {

	@Autowired
	private NiftyOiService service;

	@GetMapping("/snapshot")
	public OiSnapshot snapshot(@RequestParam(defaultValue = "500") int range) {

		return service.snapshot(range);
	}
}
