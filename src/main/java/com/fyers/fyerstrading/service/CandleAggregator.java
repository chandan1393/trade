/*
 * package com.fyers.fyerstrading.service;
 * 
 * import java.time.Duration; import java.time.LocalDateTime; import
 * java.util.ArrayList; import java.util.List;
 * 
 * import org.springframework.beans.factory.annotation.Autowired; import
 * org.springframework.scheduling.annotation.Scheduled; import
 * org.springframework.stereotype.Service;
 * 
 * import com.fyers.fyerstrading.entity.CandleEntity; import
 * com.fyers.fyerstrading.model.Candle; import
 * com.fyers.fyerstrading.repo.CandleRepository;
 * 
 * 
 * @Service public class CandleAggregator {
 * 
 * @Autowired private CandleRepository candleRepository;
 * 
 * @Autowired private EmaCrossoverEngine emaCrossoverEngine;
 * 
 * private final Duration interval = Duration.ofMinutes(5); private Candle
 * currentCandle; private final List<Candle> completedCandles = new
 * ArrayList<>(); private LocalDateTime lastTickTime;
 * 
 * public synchronized void onNewTick(double price, long volume, LocalDateTime
 * tickTime) { lastTickTime = tickTime;
 * 
 * LocalDateTime candleStart = getCandleStartTime(tickTime);
 * 
 * // Only allow tick that matches exact 5-min boundary to start candle boolean
 * isExactCandleStart = tickTime.getSecond() == 0 && tickTime.getNano() == 0 &&
 * tickTime.getMinute() % 5 == 0;
 * 
 * if (currentCandle == null) { if (!isExactCandleStart) {
 * System.out.println("🕒 Waiting for 5-min boundary. Ignoring tick: " +
 * tickTime); return; } currentCandle = new Candle(candleStart, price, volume);
 * return; }
 * 
 * // If new candle time arrived if
 * (!currentCandle.getStartTime().equals(candleStart)) {
 * finalizeCurrentCandle(); if (isExactCandleStart) { currentCandle = new
 * Candle(candleStart, price, volume); } else { currentCandle = null; // wait
 * again for correct boundary
 * System.out.println("🕒 Skipping tick not aligned to 5-min start: " +
 * tickTime); } } else { currentCandle.update(price, volume); } }
 * 
 * private void finalizeCurrentCandle() { if (currentCandle == null) return;
 * 
 * completedCandles.add(currentCandle);
 * 
 * // Save to DB CandleEntity entity = new CandleEntity();
 * entity.setStartTime(currentCandle.getStartTime());
 * entity.setOpen(currentCandle.getOpen());
 * entity.setHigh(currentCandle.getHigh());
 * entity.setLow(currentCandle.getLow());
 * entity.setClose(currentCandle.getClose());
 * entity.setVolume(currentCandle.getVolume()); candleRepository.save(entity);
 * 
 * // Trigger EMA crossover emaCrossoverEngine.onCandleClose(currentCandle);
 * 
 * System.out.println("Saved & triggered EMA for candle at: " +
 * currentCandle.getStartTime()); }
 * 
 * private LocalDateTime getCandleStartTime(LocalDateTime time) { int minute =
 * (time.getMinute() / 5) * 5; return
 * time.withMinute(minute).withSecond(0).withNano(0); }
 * 
 * @Scheduled(fixedRate = 300000) // Every 5 mins public void flushIfStale() {
 * if (currentCandle == null) return; LocalDateTime now = LocalDateTime.now();
 * if (lastTickTime != null && Duration.between(lastTickTime, now).toMinutes() >
 * 5) { System.out.println("Flushing stale candle due to inactivity.");
 * finalizeCurrentCandle(); currentCandle = null; } }
 * 
 * public void reset() { currentCandle = null; completedCandles.clear(); } }
 */