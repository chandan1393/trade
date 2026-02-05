package com.fyers.fyerstrading;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FyersTradingApplication {

	public static void main(String[] args) {
		SpringApplication.run(FyersTradingApplication.class, args);
	}

}
