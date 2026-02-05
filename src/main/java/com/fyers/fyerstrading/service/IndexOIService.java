package com.fyers.fyerstrading.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fyers.fyerstrading.entity.IndexOIData;
import com.fyers.fyerstrading.repo.IndexOIRepository;

@Service
public class IndexOIService {

	private static final String INDEX = "NSE:NIFTY50-INDEX";
	private final AtomicBoolean running = new AtomicBoolean(false);

	@Autowired
	private FyersApiService apiClient;

	@Autowired
	private IndexOIRepository oiRepository;

	
	public void fetchOi() {

		if (!running.compareAndSet(false, true))
			return;

		try {
			List<IndexOIData> data = apiClient.getIndexOptionChain(INDEX, 25, null);
			if (!data.isEmpty()) {
				oiRepository.saveAll(data);
			}
		} finally {
			running.set(false);
		}
	}
	
	
	public void fetchAndStore() {

		if (!running.compareAndSet(false, true))
			return;

		try {
			List<IndexOIData> data = apiClient.getIndexOptionChain(INDEX, 25, null);
			if (!data.isEmpty()) {
				oiRepository.saveAll(data);
			}
		} finally {
			running.set(false);
		}
	}

	public int getATMStrike(double ltp, boolean isIndex) {
		if (isIndex) {
			return (int) (Math.round(ltp / 50.0) * 50); // Nifty strikes multiple of 50
		} else {
			return (int) (Math.round(ltp / 10.0) * 10); // F&O stocks multiple of 10
		}
	}

	public void backfillIndexOI(LocalDate fromDate, LocalDate toDate) {

		LocalDate date = fromDate;

		while (!date.isAfter(toDate)) {

			LocalDateTime time = date.atTime(9, 15);

			while (time.isBefore(date.atTime(15, 30))) {

				try {
					fetchAndStoreAtTime(time); // 1-minute logic
				} catch (Exception e) {
					// log error and continue
				}

				time = time.plusMinutes(1);
			}

			date = date.plusDays(1);
		}
	}

	public void fetchAndStoreAtTime(LocalDateTime time) {

		// 🔒 Skip if already exists (idempotent)
		if (oiRepository.findBySymbolAndTimestamp(INDEX, time).size() > 0) {
			return;
		}

		List<IndexOIData> oiDataList = apiClient.getIndexOptionChain(INDEX, 25, time.toString());

		if (oiDataList == null || oiDataList.isEmpty()) {
			return;
		}

		oiRepository.saveAll(oiDataList);
	}
}
