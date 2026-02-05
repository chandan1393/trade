package com.fyers.fyerstrading.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fyers.fyerstrading.entity.OptionInstrument;
import com.fyers.fyerstrading.repo.OptionInstrumentRepository;

@Service
@Transactional
public class OptionInstrumentUpdateService {

	private static final String FYERS_URL = "https://public.fyers.in/sym_details/NSE_FO_sym_master.json";

	@Autowired
	private OptionInstrumentRepository optionRepo;

	private final ObjectMapper mapper = new ObjectMapper();

	// ================= MAIN METHOD =================
	public void refreshOptionInstruments(LocalDate today) {

		try {
			System.out.println("🔁 Refreshing option instruments for " + today);

			// 1️⃣ Delete everything (clean slate)
			optionRepo.deleteAllInBatch();

			// 2️⃣ Import fresh instruments
			List<OptionInstrument> instruments = fetchUpcomingTwoMonths(today);

			// 3️⃣ Save
			optionRepo.saveAll(instruments);

			System.out.println("✅ Option instruments loaded: " + instruments.size());

		} catch (Exception e) {
			// Fail-safe: never break scheduler
			System.err.println("❌ Option instrument refresh failed");
			e.printStackTrace();
		}
	}

	// ================= CORE LOGIC =================
	private List<OptionInstrument> fetchUpcomingTwoMonths(LocalDate today) throws IOException {

		LocalDate maxExpiry = today.plusMonths(3);

		InputStream is = new URL(FYERS_URL).openStream();
		JsonNode root = mapper.readTree(is);

		List<OptionInstrument> result = new ArrayList<>();

		// ✅ ROOT IS OBJECT (symbol → details)
		root.fields().forEachRemaining(entry -> {

			JsonNode n = entry.getValue();

			// Only options
			if (!n.hasNonNull("optType"))
				return;

			if (!n.hasNonNull("expiryDate"))
				return;

			// 🔹 Convert epoch seconds → LocalDate
			long epochSec = n.get("expiryDate").asLong();
			LocalDate expiry = Instant.ofEpochSecond(epochSec).atZone(ZoneId.of("Asia/Kolkata")).toLocalDate();

			// Keep only upcoming 2 months
			if (expiry.isBefore(today) || expiry.isAfter(maxExpiry))
				return;

			OptionInstrument oi = new OptionInstrument();
			oi.setSymbol(n.get("symTicker").asText()); // NSE:INDHOTEL25DEC380CE
			oi.setUnderlying(n.get("underSym").asText()); // INDHOTEL
			oi.setExpiry(expiry);
			oi.setStrike(n.get("strikePrice").asInt());
			oi.setOptionType(n.get("optType").asText()); // CE / PE
			oi.setLotSize(n.get("minLotSize").asInt());

			result.add(oi);
		});

		return result;
	}

}
