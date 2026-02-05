package com.fyers.fyerstrading.config;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fyers.fyerstrading.service.FyersTokenManager;
import com.fyers.fyerstrading.websocket.GeneralUpdatesWebSocketService;
import com.fyers.fyerstrading.websocket.MarketDataWebSocketService;

@Component
public class FyersStartupService {

	@Autowired
	private FyersTokenManager fyersTokenManager;

	@Autowired
	private MarketDataWebSocketService marketWebSocketService;

	@Autowired
	private GeneralUpdatesWebSocketService generalWebSocketService;

	@EventListener(ApplicationReadyEvent.class)
	public void initializeTokenAfterStartup() {

		try {
			System.out.println("Initializing Fyers system...");

			// Step 1: Refresh token
			fyersTokenManager.refreshAccessTokenOnStartup();

			// 🔥 Step 2: Force close any existing sockets
			System.out.println("Closing any existing WebSocket connections...");
			marketWebSocketService.stopSocket();
			generalWebSocketService.stopSocket();

			// 🔥 Step 3: Wait for server to release session
			Thread.sleep(8000);

			// Step 4: Start fresh connections
			if (isMarketOpenNow()) {

				System.out.println("Market is open. Starting WebSockets...");
				marketWebSocketService.startWebSocket();
				generalWebSocketService.startWebSocket();

			} else {
				System.out.println("Market is closed. WebSocket will start via scheduled job.");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Scheduled(cron = "0 10 9 * * MON-FRI")
	public void scheduledWebSocketStart() {

		try {
			System.out.println("Scheduled WebSocket Start Triggered...");

			marketWebSocketService.shutdown();
			generalWebSocketService.closeWebSocket();

			Thread.sleep(8000);

			marketWebSocketService.startWebSocket();
			generalWebSocketService.startWebSocket();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean isMarketOpenNow() {

		LocalTime now = LocalTime.now();
		DayOfWeek day = LocalDate.now().getDayOfWeek();

		return (now.isAfter(LocalTime.of(9, 14)) && now.isBefore(LocalTime.of(15, 30)))
				&& !(day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY);
	}

	@Scheduled(cron = "0 0 16 * * MON-FRI")
	public void stopWebSocketAfterMarketClose() {

		System.out.println("Market Closed! Stopping WebSockets...");

		marketWebSocketService.shutdown();
		generalWebSocketService.closeWebSocket();
	}
}
