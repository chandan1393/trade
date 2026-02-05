package com.fyers.fyerstrading.websocket;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import javax.annotation.PreDestroy;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import com.fyers.fyerstrading.model.FyersSessionContext;
import com.fyers.fyerstrading.model.OptionTrade;
import com.fyers.fyerstrading.repo.OptionTradeRepository;
import com.fyers.fyerstrading.service.LivePriceStore;
import com.fyers.fyerstrading.service.OptionTickRouter;
import com.tts.in.model.FyersClass;
import com.tts.in.websocket.FyersSocket;
import com.tts.in.websocket.FyersSocketDelegate;

import in.tts.hsjavalib.ChannelModes;

@Service
public class MarketDataWebSocketService implements FyersSocketDelegate {

	@Autowired
	private FyersSessionContext fyersSessionContext;

	@Autowired
	private OptionTickRouter tickRouter;

	@Autowired
	private OptionTradeRepository tradeRepo;

	@Autowired
	private LivePriceStore livePriceStore;

	private FyersSocket fyersSocket;

	private final AtomicBoolean isConnected = new AtomicBoolean(false);
	private final AtomicBoolean reconnecting = new AtomicBoolean(false);

	private final ScheduledExecutorService reconnectExecutor = Executors.newSingleThreadScheduledExecutor();

	private final ScheduledExecutorService heartbeatExecutor = Executors.newSingleThreadScheduledExecutor();

	private final List<String> subscribedSymbols = new CopyOnWriteArrayList<>();

	private volatile long lastTickTime = System.currentTimeMillis();
	private volatile int reconnectAttempts = 0;

	/* ================= START AFTER APP READY ================= */

	@EventListener(ApplicationReadyEvent.class)
	public void startAfterBoot() {
		reconnectExecutor.schedule(this::startWebSocket, 5, TimeUnit.SECONDS);
	}

	/* ================= START SOCKET ================= */

	public synchronized void startWebSocket() {

		if (isConnected.get()) {
			System.out.println("WebSocket already connected.");
			return;
		}

		try {

			closeSocketIfExists();

			System.out.println("Starting MarketData WebSocket...");

			FyersClass fyersClass = FyersClass.getInstance();
			fyersClass.clientId = fyersSessionContext.getClientId();
			fyersClass.accessToken = fyersSessionContext.getAccessToken();

			System.out.println("Using Token: " + fyersClass.accessToken);

			fyersSocket = new FyersSocket(3);
			fyersSocket.webSocketDelegate = this;

			subscribedSymbols.clear();
			subscribedSymbols.addAll(getAllSymbolsFromOptionTrade());

			fyersSocket.ConnectHSM(ChannelModes.FULL);

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
		lastTickTime = System.currentTimeMillis();

		System.out.println("✅ WebSocket Connected: " + status);

		if (!subscribedSymbols.isEmpty()) {
			fyersSocket.SubscribeData(subscribedSymbols);
			System.out.println("Subscribed symbols: " + subscribedSymbols);
		}

		startHeartbeatMonitor();
	}

	@Override
	public void OnClose(String status) {
		System.err.println("❌ WebSocket Closed: " + status);
		isConnected.set(false);
		retryConnection(status);
	}

	@Override
	public void OnError(JSONObject error) {
		System.err.println("❌ WebSocket Error: " + error);
		isConnected.set(false);
		retryConnection(error.toString());
	}

	/* ================= MARKET DATA ================= */

	@Override
	public void OnScrips(JSONObject json) {

		String symbol = json.optString("symbol");
		double ltp = json.optDouble("ltp", 0);

		if (ltp <= 0 || symbol == null || symbol.isEmpty())
			return;

		lastTickTime = System.currentTimeMillis();

		livePriceStore.updatePrice(symbol, ltp);
		tickRouter.processLiveTick(symbol, ltp);
	}

	@Override
	public void OnIndex(JSONObject index) {

		double ltp = index.optDouble("ltp", 0);
		if (ltp <= 0)
			return;

		lastTickTime = System.currentTimeMillis();
		livePriceStore.updatePrice("NSE:NIFTY50-INDEX", ltp);
	}

	@Override
	public void OnOrder(JSONObject order) {
	}

	@Override
	public void OnTrade(JSONObject trades) {
	}

	@Override
	public void OnDepth(JSONObject depths) {
	}

	@Override
	public void OnPosition(JSONObject positions) {
	}

	@Override
	public void OnMessage(JSONObject message) {
	}

	/* ================= HEARTBEAT ================= */

	private void startHeartbeatMonitor() {

		heartbeatExecutor.scheduleAtFixedRate(() -> {

			if (!isConnected.get())
				return;

			long now = System.currentTimeMillis();

			if (now - lastTickTime > 60000) { // 60 sec no tick
				System.err.println("⚠ No ticks for 60s. Reconnecting safely...");
				isConnected.set(false);
				retryConnection("Heartbeat timeout");
			}

		}, 30, 30, TimeUnit.SECONDS);
	}

	/* ================= RECONNECT ================= */

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

			} catch (Exception ignored) {
			}

			System.out.println("🔁 Reconnecting after clean close...");
			startWebSocket();

		}, delay, TimeUnit.SECONDS);
	}

	/* ================= SYMBOL DISCOVERY ================= */

	private List<String> getAllSymbolsFromOptionTrade() {

		List<String> symbols = tradeRepo.findAllOpen().stream().map(OptionTrade::getSymbol).distinct()
				.collect(Collectors.toList());

		if (!symbols.contains("NSE:NIFTY50-INDEX")) {
			symbols.add("NSE:NIFTY50-INDEX");
		}

		return symbols;
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
	public void shutdown() {
		stopSocket();
		reconnectExecutor.shutdownNow();
		heartbeatExecutor.shutdownNow();
	}

	public synchronized void subscribeNewSymbol(String symbol) {

		if (symbol == null || symbol.isBlank())
			return;

		if (!subscribedSymbols.contains(symbol)) {
			subscribedSymbols.add(symbol);
			System.out.println("Symbol added to subscription list: " + symbol);
		}

		// If socket is connected → subscribe immediately
		if (isConnected.get() && fyersSocket != null) {
			try {
				fyersSocket.SubscribeData(List.of(symbol));
				System.out.println("Subscribed dynamically: " + symbol);
			} catch (Exception e) {
				System.err.println("Subscribe error for " + symbol + ": " + e.getMessage());
			}
		}
	}

	public synchronized void unsubscribeSymbol(String symbol) {

		if (symbol == null || symbol.isBlank())
			return;

		if (subscribedSymbols.remove(symbol)) {

			System.out.println("Removed from subscription list: " + symbol);

			if (isConnected.get() && fyersSocket != null) {
				try {
					fyersSocket.UnSubscribeData(List.of(symbol));
					System.out.println("Unsubscribed dynamically: " + symbol);
				} catch (Exception e) {
					System.err.println("Unsubscribe error for " + symbol + ": " + e.getMessage());
				}
			}
		}
	}

	public synchronized void stopSocket() {
		try {
			if (fyersSocket != null) {
				fyersSocket.Close();
				fyersSocket = null;
			}
			isConnected.set(false);
			reconnecting.set(false);
			System.out.println("MarketData socket stopped safely.");
		} catch (Exception ignored) {
		}
	}

}
