package com.fyers.fyerstrading.service;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.cookie.BasicCookieStore;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.springframework.stereotype.Service;

import com.fyers.fyerstrading.model.BhavcopyEntry;

@Service
public class BhavcopyDownloader {

	private static final String BASE_URL = "https://nsearchives.nseindia.com/products/content/sec_bhavdata_full_";
    private static final String DOWNLOAD_DIR = "D:/nse_bhavcopy/";
    
    public Path downloadCSV(LocalDate date) throws Exception {
        String dateStr = date.format(DateTimeFormatter.ofPattern("ddMMyyyy"));
        String fileName = "bhavcopy_" + dateStr + ".csv";
        Path downloadDirPath = Paths.get(DOWNLOAD_DIR);

        // Create download directory if it doesn't exist
        if (!Files.exists(downloadDirPath)) {
            Files.createDirectories(downloadDirPath);
        }

        // Final file path
        Path filePath = downloadDirPath.resolve(fileName);

        // If file already exists, return its path
        if (Files.exists(filePath)) {
            System.out.println("File already exists: " + filePath);
            return filePath.toAbsolutePath();
        }

        // Construct download URL
        String url = String.format("https://nsearchives.nseindia.com/products/content/sec_bhavdata_full_%s.csv", dateStr);

        BasicCookieStore cookieStore = new BasicCookieStore();

        HttpClient client = HttpClients.custom()
                .setDefaultCookieStore(cookieStore)
                .build();

        HttpGet get = new HttpGet(url);
        get.setHeader("User-Agent", "Mozilla/5.0");
        get.setHeader("Accept", "text/csv");

        ClassicHttpResponse response = (ClassicHttpResponse) client.execute(get);
        int statusCode = response.getCode();

        if (statusCode == 200) {
            HttpEntity entity = response.getEntity();
            try (InputStream in = entity.getContent();
                 FileOutputStream out = new FileOutputStream(filePath.toFile())) {
                in.transferTo(out);
                System.out.println("Downloaded successfully: " + filePath);
            }
            EntityUtils.consume(entity);
        } else {
            throw new RuntimeException("Failed to download file, HTTP Status: " + statusCode);
        }

        return filePath.toAbsolutePath();
    }

    
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd-MMM-yyyy", Locale.ENGLISH);

    public List<BhavcopyEntry> parseFullBhavcopyCsv(String csvPath) {
        try (Stream<String> lines = Files.lines(Paths.get(csvPath))) {
            return lines.skip(1) // skip header
                    .map(this::parseLine)
                    .filter(Objects::nonNull)
                    .toList();
        } catch (IOException e) {
            System.err.println("Error reading Bhavcopy CSV: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    private BhavcopyEntry parseLine(String line) {
        try {
            String[] parts = line.split(",");

            if (parts.length < 15) {
                return null; // skip invalid row
            }

            String series = parts[1].trim();
            if (!"EQ".equalsIgnoreCase(series) && !"BE".equalsIgnoreCase(series)) {
                return null; // only EQ and BE
            }

            BhavcopyEntry entry = new BhavcopyEntry();
            entry.setSymbol("NSE:".concat(parts[0].trim()).concat("-EQ"));
            entry.setSeries(series);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy", Locale.ENGLISH);
            entry.setTradeDate(LocalDate.parse(parts[2].trim(), formatter));

            entry.setPrevClose(parseDoubleSafe(parts[3]));
            entry.setOpenPrice(parseDoubleSafe(parts[4]));
            entry.setHighPrice(parseDoubleSafe(parts[5]));
            entry.setLowPrice(parseDoubleSafe(parts[6]));
            entry.setLastPrice(parseDoubleSafe(parts[7]));
            entry.setClosePrice(parseDoubleSafe(parts[8]));
            entry.setAvgPrice(parseDoubleSafe(parts[9]));
            entry.setTotalTradedQty(parseLongSafe(parts[10]));
            entry.setTurnoverInLacs(parseDoubleSafe(parts[11]));
            entry.setNumberOfTrades(parseIntSafe(parts[12]));
            entry.setDeliveryQty(parseLongSafe(parts[13]));
            entry.setDeliveryPercent((int) parseDoubleSafe(parts[14]));

            return entry;

        } catch (Exception e) {
            System.out.println("Error parsing line: " + line + " => " + e.getMessage());
            return null;
        }
    }

    // helpers to handle "-" gracefully
    private double parseDoubleSafe(String val) {
        return (val == null || val.trim().equals("-") || val.isBlank()) ? 0.0 : Double.parseDouble(val.trim());
    }

    private long parseLongSafe(String val) {
        return (val == null || val.trim().equals("-") || val.isBlank()) ? 0L : Long.parseLong(val.trim());
    }

    private int parseIntSafe(String val) {
        return (val == null || val.trim().equals("-") || val.isBlank()) ? 0 : Integer.parseInt(val.trim());
    }


    
}
