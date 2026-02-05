package com.fyers.fyerstrading.service;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fyers.fyerstrading.entity.TradeSetup;
import com.fyers.fyerstrading.entity.TradeStatus;
import com.fyers.fyerstrading.repo.TradeSetupRepository;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

@Component
public class DeliveryUpdaterService {

	@Autowired
	TradeSetupRepository tradeSetupRepository;
	@Autowired
	BhavcopyDownloader bhavcopyDownloader;

	private static final String BASE_URL = "https://nsearchives.nseindia.com/products/content/sec_bhavdata_full_";
	private static final String DOWNLOAD_DIR = "D:/nse_bhavcopy/";

	

	private String downloadBhavcopy(LocalDate date) {
		String dateStr = date.format(DateTimeFormatter.ofPattern("ddMMyyyy"));
		String fileName = "bhavcopy_" + dateStr + ".csv";
		String fullPath = DOWNLOAD_DIR + fileName;

		try {
			Files.createDirectories(Paths.get(DOWNLOAD_DIR));
			URL url = new URL(BASE_URL + dateStr + ".csv");

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("User-Agent", "Mozilla/5.0");

			if (conn.getResponseCode() != 200) {
				System.out.println("Failed to download: " + url);
				return null;
			}

			try (InputStream in = conn.getInputStream(); FileOutputStream out = new FileOutputStream(fullPath)) {
				in.transferTo(out);
				return fullPath;
			}

		} catch (IOException e) {
			System.out.println("Error downloading bhavcopy for " + date + ": " + e.getMessage());
			return null;
		}
	}

	private Map<String, Integer> parseDeliveryFromCsv(String csvPath) {
	    Map<String, Integer> deliveryMap = new HashMap<>();

	    try (BufferedReader reader = new BufferedReader(new FileReader(csvPath))) {
	        String header = reader.readLine(); // Skip header
	        String line;

	        while ((line = reader.readLine()) != null) {
	            // Split on comma instead of tab
	            String[] parts = line.split(",", -1); // -1 preserves empty trailing fields

	            if (parts.length < 15) continue;

	            String symbol = parts[0].trim();
	            String deliveryStr = parts[14].trim();

	            try {
	                // Parse as double and round to int
	                double percent = Double.parseDouble(deliveryStr);
	                deliveryMap.put(symbol, (int) Math.round(percent));
	            } catch (NumberFormatException ignored) {}
	        }
	    } catch (IOException e) {
	        System.out.println("CSV Parse Error: " + e.getMessage());
	    }

	    return deliveryMap;
	}



	private String extractSymbol(String dbSymbol) {
		if (dbSymbol.startsWith("NSE:") && dbSymbol.endsWith("-EQ")) {
			return dbSymbol.substring(4, dbSymbol.length() - 3);
		}
		return dbSymbol;
	}
}
