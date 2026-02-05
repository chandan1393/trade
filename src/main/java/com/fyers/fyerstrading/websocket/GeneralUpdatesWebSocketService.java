package com.fyers.fyerstrading.websocket;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.PreDestroy;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fyers.fyerstrading.model.FyersSessionContext;
import com.fyers.fyerstrading.model.Position;
import com.fyers.fyerstrading.service.OptionTradeLifecycleManager;
import com.fyers.fyerstrading.service.VolumePriceBreakOutService;
import com.tts.in.model.FyersClass;
import com.tts.in.websocket.FyersSocket;
import com.tts.in.websocket.FyersSocketDelegate;

@Service
public class GeneralUpdatesWebSocketService implements FyersSocketDelegate {

	private FyersSocket fyersSocket;

	@Autowired
	private FyersSessionContext fyersSessionContext;

	@Autowired
	private VolumePriceBreakOutService vpBreakOutService;

	@Autowired
	private OptionTradeLifecycleManager manager;

	private final AtomicBoolean isConnected = new AtomicBoolean(false);
	private final AtomicBoolean reconnecting = new AtomicBoolean(false);

	private final ScheduledExecutorService reconnectExecutor = Executors.newSingleThreadScheduledExecutor();

	private volatile int reconnectAttempts = 0;

	// Rate-limit broker sync
	private volatile long lastSyncTime = 0;
	private static final long SYNC_INTERVAL_MS = 3000;

	/* ================= START AFTER APP READY ================= */

	@EventListener(ApplicationReadyEvent.class)
	public void startAfterBoot() {
		reconnectExecutor.schedule(this::startWebSocket, 5, TimeUnit.SECONDS);
	}

	/* ================= START SOCKET ================= */

	public synchronized void startWebSocket() {

		if (isConnected.get()) {
			System.out.println("GeneralUpdates WebSocket already connected.");
			return;
		}

		try {

			closeSocketIfExists();

			System.out.println("Starting GeneralUpdates WebSocket...");

			FyersClass fyersClass = FyersClass.getInstance();
			fyersClass.clientId = fyersSessionContext.getClientId();
			fyersClass.accessToken = fyersSessionContext.getAccessToken();

			System.out.println("Using Token: " + fyersClass.accessToken);

			fyersSocket = new FyersSocket(3);
			fyersSocket.webSocketDelegate = this;

			fyersSocket.Connect();

		} catch (Exception e) {
			System.err.println("Start Error: " + e.getMessage());
			retryConnection(e.getMessage());
		}
	}

	/* ================= CALLBACKS ================= */

	@Override
	public void OnOpen(String status) {

		isConnected.set(true);
		reconnecting.set(false);
		reconnectAttempts = 0;

		System.out.println("✅ GeneralUpdates WebSocket Connected: " + status);

		// Subscribe AFTER open (IMPORTANT)
		fyersSocket.Subscribe(List.of("orders", "positions", "trades"));
	}

	@Override
	public void OnPosition(JSONObject positions) {

		try {

			long now = Instant.now().toEpochMilli();

			if (now - lastSyncTime > SYNC_INTERVAL_MS) {
				manager.syncFromBroker();
				lastSyncTime = now;
			}

			Position position = new ObjectMapper().readValue(positions.toString(), Position.class);

			vpBreakOutService.processTrade(position);

		} catch (Exception e) {
			System.err.println("❌ Error processing position update");
			e.printStackTrace();
		}
	}

	@Override
	public void OnOrder(JSONObject orderJson) {
		System.out.println("Order Update: " + orderJson);
	}

	@Override
	public void OnTrade(JSONObject tradeJson) {
		System.out.println("Trade Update: " + tradeJson);
	}

	@Override
	public void OnMessage(JSONObject message) {
		System.out.println("Message: " + message);
	}

	@Override
	public void OnError(JSONObject error) {
		System.err.println("❌ WebSocket Error: " + error);
		isConnected.set(false);
		retryConnection(error.toString());
	}

	@Override
	public void OnClose(String status) {
		System.err.println("❌ WebSocket Closed: " + status);
		isConnected.set(false);
		retryConnection(status);
	}

	/* ================= SAFE RECONNECT ================= */

	private void retryConnection(String reason) {

	    if (!reconnecting.compareAndSet(false, true))
	        return;

	    reconnectAttempts++;

	    int delay = 30; // base delay

	    if (reason != null && reason.contains("Max-Connection")) {
	        delay = 90; // Fyers needs longer release time
	        System.err.println("⚠ Max connection limit hit. Waiting 90 seconds...");
	    }

	    reconnectExecutor.schedule(() -> {

	        try {

	            System.out.println("Closing old socket before reconnect...");

	            if (fyersSocket != null) {
	                fyersSocket.Close();
	                fyersSocket = null;
	            }

	            Thread.sleep(8000); // 🔥 VERY IMPORTANT

	        } catch (Exception ignored) {}

	        System.out.println("🔁 Reconnecting after clean close...");
	        startWebSocket();

	    }, delay, TimeUnit.SECONDS);
	}


	/* ================= CLEANUP ================= */

	private void closeSocketIfExists() {
		try {
			if (fyersSocket != null) {
				fyersSocket.Close();
			}
		} catch (Exception ignored) {
		}
	}

	@PreDestroy
	public void closeWebSocket() {

		System.out.println("Shutting down GeneralUpdates WebSocket...");

		closeSocketIfExists();
		reconnectExecutor.shutdownNow();
	}

	@Override
	public void OnDepth(JSONObject arg0) {
	}

	@Override
	public void OnIndex(JSONObject arg0) {
	}

	@Override
	public void OnScrips(JSONObject arg0) {
	}
	public synchronized void stopSocket() {
	    try {
	        if (fyersSocket != null) {
	            fyersSocket.Close();
	            fyersSocket = null;
	        }
	        isConnected.set(false);
	        reconnecting.set(false);
	        System.out.println("GeneralUpdates socket stopped safely.");
	    } catch (Exception ignored) {}
	}

	
}
