package com.fyers.fyerstrading.service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.json.JSONObject;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fyers.fyerstrading.entity.FNO5MinCandle;
import com.fyers.fyerstrading.entity.IndexCategory;
import com.fyers.fyerstrading.entity.Nifty5MinCandle;
import com.fyers.fyerstrading.entity.NiftyDailyCandle;
import com.fyers.fyerstrading.entity.StockDailyPrice;
import com.fyers.fyerstrading.entity.StockMaster;
import com.fyers.fyerstrading.entity.StockTechnicalIndicator;
import com.fyers.fyerstrading.model.BhavcopyEntry;
import com.fyers.fyerstrading.model.StockCandle;
import com.fyers.fyerstrading.model.StockData;
import com.fyers.fyerstrading.repo.FNO5MinCandleRepository;
import com.fyers.fyerstrading.repo.IndexCategoryRepository;
import com.fyers.fyerstrading.repo.NiftyDailyCandleRepo;
import com.fyers.fyerstrading.repo.NiftyFiveMinCandleRepository;
import com.fyers.fyerstrading.repo.StockDailyPriceRepository;
import com.fyers.fyerstrading.repo.StockIntradayDataRepository;
import com.fyers.fyerstrading.repo.StockMasterRepository;
import com.fyers.fyerstrading.repo.StockTechnicalIndicatorRepository;
import com.fyers.fyerstrading.utility.CalculationUtil;
import com.fyers.fyerstrading.utility.TradingUtil;
import com.fyers.fyerstrading.utility.Util;
import com.tts.in.model.StockHistoryModel;

@Service
public class StockDataService {

	private final FyersApiService fyersApiService;
	private final StockDailyPriceRepository stockDailyPriceRepository;
	private final StockMasterRepository stockMasterRepository;
	private final StockTechnicalIndicatorRepository indicatorRepository;
	private final NiftyFiveMinCandleRepository niftyFiveMinCandleRepository;
	private final IndexCategoryRepository indexCategoryRepository;
	private final CalculationUtil calculationUtil;
	private final StockIntradayDataRepository intradayDataRepository;
	private final HelperClassForCalculation helperClassForCalculation;
	private final NiftyDailyCandleRepo niftyDailyCandleRepo;
	private final BhavcopyDownloader bhavcopyDownloader;
	private final NiftyEmaCrossoverService niftyEmaCrossoverService;
	private final FNO5MinCandleRepository fno5MinCandleRepository;

	public StockDataService(FyersApiService fyersApiService, StockDailyPriceRepository stockDailyPriceRepository,
			StockMasterRepository stockMasterRepository, StockTechnicalIndicatorRepository indicatorRepository,
			NiftyFiveMinCandleRepository niftyFiveMinCandleRepository, IndexCategoryRepository indexCategoryRepository,
			CalculationUtil calculationUtil, StockIntradayDataRepository intradayDataRepository,
			HelperClassForCalculation helperClassForCalculation, NiftyDailyCandleRepo niftyDailyCandleRepo,
			BhavcopyDownloader bhavcopyDownloader, NiftyEmaCrossoverService niftyEmaCrossoverService,
			FNO5MinCandleRepository fno5MinCandleRepository) {
		this.fyersApiService = fyersApiService;
		this.stockDailyPriceRepository = stockDailyPriceRepository;
		this.stockMasterRepository = stockMasterRepository;
		this.indicatorRepository = indicatorRepository;
		this.niftyFiveMinCandleRepository = niftyFiveMinCandleRepository;
		this.indexCategoryRepository = indexCategoryRepository;
		this.calculationUtil = calculationUtil;
		this.intradayDataRepository = intradayDataRepository;
		this.helperClassForCalculation = helperClassForCalculation;
		this.niftyDailyCandleRepo = niftyDailyCandleRepo;
		this.bhavcopyDownloader = bhavcopyDownloader;
		this.niftyEmaCrossoverService = niftyEmaCrossoverService;
		this.fno5MinCandleRepository = fno5MinCandleRepository;
	}

	private static final String NSE_STOCK_URL = "https://public.fyers.in/sym_details/NSE_CM_sym_master.json";
	private static final String BSE_STOCK_URL = "https://public.fyers.in/sym_details/BSE_CM_sym_master.json";
	private static final String FILE_PATH = "C:\\Users\\DELL\\Downloads\\MW-NIFTY-MIDCAP-SELECT-02-Apr-2025.csv";

	public void fetchAndSaveStocks() {
		try {
			// Fetch JSON data
			RestTemplate restTemplate = new RestTemplate();
			String jsonData = restTemplate.getForObject(BSE_STOCK_URL, String.class);

			// Parse JSON into a Map
			ObjectMapper objectMapper = new ObjectMapper();
			Map<String, StockData> stockMap = objectMapper.readValue(jsonData,
					objectMapper.getTypeFactory().constructMapType(Map.class, String.class, StockData.class));

			// Separate Stocks & Indices
			Map<String, StockData> eqStocks = stockMap.entrySet().stream()
					.filter(entry -> entry.getKey().endsWith("-EQ"))
					.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

			Map<String, StockData> indexStocks = stockMap.entrySet().stream()
					.filter(entry -> entry.getKey().endsWith("-INDEX"))
					.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

			// ✅ Save Stocks (-EQ)
			for (StockData stock : eqStocks.values()) {
				if (!stockMasterRepository.existsByIsinCode(stock.getIsin())) {
					StockMaster stockMaster = new StockMaster();
					stockMaster.setSymbol(stock.getSymTicker());
					stockMaster.setCompanyName(stock.getExSymName());
					stockMaster.setExchange(stock.getExchangeName());
					stockMaster.setIsinCode(stock.getIsin());
					stockMaster.setSeries(stock.getExSeries());
					stockMaster.setAddedDate(LocalDateTime.now());

					stockMasterRepository.save(stockMaster);
				}
			}

			// ✅ Save Indices (-INDEX)
			for (StockData index : indexStocks.values()) {
				if (!indexCategoryRepository.existsBySymbol(index.getSymTicker())) {
					IndexCategory indexCategory = new IndexCategory();
					indexCategory.setSymbol(index.getSymTicker());
					indexCategory.setIndexName(index.getExSymName());
					indexCategory.setIndexType("Unknown"); // No indexType in JSON
					indexCategory.setDescription("No description available");

					indexCategoryRepository.save(indexCategory);
				}
			}

			System.out.println("Stocks & Indices saved successfully!");

		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Error fetching or saving stock data!");
		}
	}

	public List<StockCandle> getHistoricalData(StockHistoryModel model) {
		JSONObject jSONObject = fyersApiService.getStockHistory(model);
		List<StockCandle> candleBatch = Util.parseStockData(jSONObject);
		return candleBatch;
	}

	@Transactional
	public void fetchAndUpdateCurrentDayStockData(String timeframe, StockMaster stock) {
		try {
			// ✅ Fetch latest available record for previous values
			StockTechnicalIndicator latestIndicator = indicatorRepository
					.findTopByStock(stock.getId(), PageRequest.of(0, 1)).stream().findFirst().orElse(null);

			StockDailyPrice latestDailyPrice = stockDailyPriceRepository
					.findByStockIdOrderByTradeDateDesc(stock.getId(), PageRequest.of(0, 1)).stream().findFirst()
					.orElse(null);

			if (latestDailyPrice == null) {
				System.out.println("No existing data for: " + stock.getSymbol() + ". Fetching full history.");
				return;
			}

			LocalDate startDate = latestDailyPrice.getTradeDate().plusDays(1);
			LocalDate endDate = LocalDate.now();
			LocalTime now = LocalTime.now();

			if (startDate.equals(LocalDate.now()) && now.isBefore(LocalTime.of(17, 0)))
				return;

			if (now.isAfter(LocalTime.of(17, 0))) { // 17:00 means 5 PM
				endDate = LocalDate.now();
			} else {
				endDate = LocalDate.now().minusDays(1);
			}

			if (startDate.isAfter(endDate)) {
				System.out.println("Stock data for " + stock.getSymbol() + " is already up-to-date.");
				return;
			}

			// ✅ Prepare request for missing data
			StockHistoryModel model = helperClassForCalculation.prepareRequestModel(stock.getSymbol(), timeframe,
					startDate, endDate);
			List<StockCandle> candles = getHistoricalData(model);

			// ✅ Convert & Filter Data
			List<StockDailyPrice> priceList = new ArrayList<>();
			List<StockTechnicalIndicator> indicatorList = new ArrayList<>();

			for (StockCandle candle : candles) {
				StockDailyPrice price = new StockDailyPrice();
				price.setStock(stock);
				price.setTradeDate(candle.getDate());
				price.setOpenPrice(candle.getOpen());
				price.setHighPrice(candle.getHigh());
				price.setLowPrice(candle.getLow());
				price.setClosePrice(candle.getClose());
				price.setVolume(candle.getVolume());

				// ✅ Calculate technical indicators
				StockTechnicalIndicator indicator = helperClassForCalculation.calculateIndicators(price,
						latestIndicator);
				priceList.add(price);
				indicatorList.add(indicator);

				latestIndicator = indicator; // Update for next iteration
			}

			// ✅ Save Data in Bulk
			stockDailyPriceRepository.saveAll(priceList);
			indicatorRepository.saveAll(indicatorList);
			System.out.println("Updated stock data for: " + stock.getSymbol());

		} catch (Exception e) {
			System.err.println("Error updating data for " + stock.getSymbol() + ": " + e.getMessage());
		}
	}

	@Transactional
	public void fetchAndSaveAllHistoricalData(StockMaster stock, String timeframe) {
		try {
			if (stock == null) {
				throw new RuntimeException("Stock cannot be null.");
			}
			LocalDate endDate = LocalDate.now(); // Start from today
			LocalDate startDate = endDate.minusDays(300); // Fetch for 300 days at a time

			Optional<LocalDate> startDateInDb = stockDailyPriceRepository.findStartDate(stock.getId());
			if (startDateInDb.isPresent()) {
				LocalDate startDateDb = startDateInDb.get();
				endDate = startDateDb.minusDays(1); // Start from today
				startDate = startDateDb.minusDays(330); // Fetch for 300 days at a time
			}

			// ✅ Fetch existing dates from DB to avoid duplicates
			Set<LocalDate> uniqueDates = new HashSet<>(
					stockDailyPriceRepository.findExistingDatesBySymbol(stock.getSymbol()));

			List<StockDailyPrice> allRecords = new ArrayList<>();
			int noDataCount = 0;

			while (startDate.isBefore(endDate)) {
				StockHistoryModel request = helperClassForCalculation.prepareRequestModel(stock.getSymbol(), timeframe,
						startDate, endDate);
				List<StockCandle> candles = getHistoricalData(request);

				if (candles.isEmpty()) {
					System.out.println(
							"No data found for: " + stock.getSymbol() + " in range " + startDate + " to " + endDate);
					noDataCount++;
					if (noDataCount >= 1) { // Stop after 5 consecutive empty responses
						System.out.println("No more historical data available. Stopping...");
						break;
					}
				} else {
					noDataCount = 0; // Reset counter if data is found

					for (StockCandle candle : candles) {
						if (uniqueDates.add(candle.getDate())) { // If new date, add to list
							allRecords.add(new StockDailyPrice(stock, candle.getDate(), candle.getOpen(),
									candle.getHigh(), candle.getLow(), candle.getClose(), candle.getVolume()));
						}
					}

				}
				if (candles != null && !candles.isEmpty()) {
					// ✅ Always move date range after processing
					endDate = startDate.minusDays(1);
					startDate = endDate.minusDays(330);
				}

			}

			// ✅ Save all collected records in one batch operation
			if (!allRecords.isEmpty()) {
				stockDailyPriceRepository.saveAll(allRecords);
				System.out.println("Saved " + allRecords.size() + " records for " + stock.getSymbol());
			} else {
				System.out.println("No new data found for " + stock.getSymbol());
			}

			System.out.println("Completed fetching historical data for: " + stock.getSymbol());
		} catch (Exception e) {
			System.err.println("Error fetching historical data for " + stock.getSymbol() + ": " + e.getMessage());
		}
	}

	@Transactional
	public void fetchAndSaveNiftyDailyData(String symbol) {
		try {
			if (symbol.isEmpty()) {
				throw new RuntimeException("Stock cannot be null.");
			}

			List<NiftyDailyCandle> allRecords = new ArrayList<>();
			Optional<LocalDate> latestDate = niftyDailyCandleRepo.findLatestDate();

			LocalDate startDate = LocalDate.of(1980, 1, 1);
			if (latestDate.isPresent()) {
				startDate = latestDate.get().plusDays(1);

			}
			LocalDate endDate = LocalDate.now();
			LocalTime now = LocalTime.now();

			if (now.isAfter(LocalTime.of(17, 0))) { // 17:00 means 5 PM
				endDate = LocalDate.now();
			} else {
				endDate = LocalDate.now().minusDays(1);
			}
			StockHistoryModel request = helperClassForCalculation.prepareRequestModel(symbol, "1DAY", startDate,
					endDate);
			List<StockCandle> candles = getHistoricalData(request);

			for (StockCandle candle : candles) {
				allRecords.add(new NiftyDailyCandle(candle.getOpen(), candle.getHigh(), candle.getLow(),
						candle.getClose(), candle.getDate()));

			}
			niftyDailyCandleRepo.saveAll(allRecords);

		} catch (Exception e) {
			System.err.println(" Error fetching data for " + symbol + ": " + e.getMessage());
		}
	}

	@Transactional
	public void downloadAndSaveNiftyHistoricalData() {
		int batchSize = 90;
		String symbol = "NSE:NIFTY50-INDEX";
		String timeframe = "5MIN";

		LocalDate startDate = LocalDate.of(2017, 7, 3);

		// Resume from last saved candle if exists
		Optional<Nifty5MinCandle> lastSavedOpt = niftyFiveMinCandleRepository.findTop1ByOrderByTimestampDesc();
		if (lastSavedOpt.isPresent()) {
			startDate = lastSavedOpt.get().getTimestamp().toLocalDate().plusDays(1);
		}

		LocalDate lastPossibleDate = LocalDate.now().minusDays(1);
		// Check current time
		LocalTime now = LocalTime.now();

		// If after 5 PM → use today, else yesterday
		if (now.isAfter(LocalTime.of(17, 0))) {
			lastPossibleDate = LocalDate.now();
		} else {
			lastPossibleDate = LocalDate.now().minusDays(1);
		}

		while (!startDate.isAfter(lastPossibleDate)) {
			LocalDate batchEndDate = startDate.plusDays(batchSize);
			if (batchEndDate.isAfter(lastPossibleDate)) {
				batchEndDate = lastPossibleDate;
			}

			StockHistoryModel model = helperClassForCalculation.prepareRequestModel(symbol, timeframe, startDate,
					batchEndDate);

			List<StockCandle> candles = getHistoricalData(model);

			if (!candles.isEmpty()) {
				LocalDateTime minDate = LocalDateTime.ofInstant(candles.get(0).getTimestamp(),
						ZoneId.of("Asia/Kolkata"));
				LocalDateTime maxDate = LocalDateTime.ofInstant(candles.get(candles.size() - 1).getTimestamp(),
						ZoneId.of("Asia/Kolkata"));

				// Fetch existing timestamps only once for this batch
				Set<LocalDateTime> existingTimestamps = new HashSet<>(
						niftyFiveMinCandleRepository.findAllTimestampsBetween(minDate, maxDate));

				// Filter out duplicates
				List<Nifty5MinCandle> niftyDataList = candles.stream().map(candle -> {
					LocalDateTime candleTime = LocalDateTime.ofInstant(candle.getTimestamp(),
							ZoneId.of("Asia/Kolkata"));
					return new Nifty5MinCandle(candle.getOpen(), candle.getHigh(), candle.getLow(), candle.getClose(),
							candleTime);
				}).filter(c -> !existingTimestamps.contains(c.getTimestamp())).collect(Collectors.toList());

				if (!niftyDataList.isEmpty()) {
					niftyFiveMinCandleRepository.saveAll(niftyDataList);
					updateEmaForNewCandles();
					System.out.println("✅ Saved new candles: " + niftyDataList.size());
				} else {
					System.out.println("No new candles to save (all duplicates).");
				}
			}

			System.out.println("Processed data from " + startDate + " to " + batchEndDate);
			startDate = batchEndDate.plusDays(1);
		}
	}

	@Transactional
	public void calculateAndSaveEmaForAllCandles() {
		// 1. Fetch all candles ordered by time
		List<Nifty5MinCandle> candles = niftyFiveMinCandleRepository.findAllByOrderByTimestampAsc();

		if (candles.isEmpty()) {
			System.out.println(" No candles found in DB.");
			return;
		}

		// 2. EMA multipliers
		double multiplier9 = 2.0 / (9 + 1);
		double multiplier21 = 2.0 / (21 + 1);

		// 3. Seed values: use SMA for first N periods
		double ema9 = candles.stream().limit(9).mapToDouble(Nifty5MinCandle::getClose).average().orElse(0);
		double ema21 = candles.stream().limit(21).mapToDouble(Nifty5MinCandle::getClose).average().orElse(0);

		// Assign to first valid candle
		for (int i = 0; i < candles.size(); i++) {
			Nifty5MinCandle candle = candles.get(i);
			double close = candle.getClose();

			if (i >= 9) {
				ema9 = ((close - ema9) * multiplier9) + ema9;
				candle.setEma9(ema9);
			} else {
				candle.setEma9(ema9); // seed SMA value
			}

			if (i >= 21) {
				ema21 = ((close - ema21) * multiplier21) + ema21;
				candle.setEma21(ema21);
			} else {
				candle.setEma21(ema21); // seed SMA value
			}
		}

		// 4. Save all candles back
		niftyFiveMinCandleRepository.saveAll(candles);

		System.out.println(" Full EMA9 & EMA21 recalculated for " + candles.size() + " candles.");
	}

	@Transactional
	public void mapStocksToIndex() {
		try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
			String line;
			boolean firstLine = true; // Skip header row

			// Fetch the "NIFTY 50" index category
			Optional<IndexCategory> nifty50IndexOptinal = indexCategoryRepository.findBySymbol("NSE:MIDCPNIFTY-INDEX");
			if (nifty50IndexOptinal.isPresent()) {
				IndexCategory nifty50Index = nifty50IndexOptinal.get();

				Set<StockMaster> nifty50Stocks = new HashSet<>();

				while ((line = br.readLine()) != null) {
					if (firstLine) {
						firstLine = false;
						continue; // Skip the header row
					}

					String[] data = line.split(",");
					if (data.length < 5) {
						System.err.println("Skipping invalid row: " + line);
						continue;
					}

					String symbol = "NSE:".concat(data[1].trim()).concat("-EQ");
					String isinCode = data[4].trim();

					// Find stock by symbol and ISIN code
					Optional<StockMaster> stockOpt = stockMasterRepository.findBySymbol(symbol);
					if (stockOpt.isPresent()) {
						nifty50Stocks.add(stockOpt.get());
					} else {
						System.out.println("Stock not found in DB: " + symbol);
					}
				}

				// Update the index category with the mapped stocks
				nifty50Index.setStocks(nifty50Stocks);
				indexCategoryRepository.save(nifty50Index);

				System.out.println("NIFTY 50 stocks mapped successfully!");
			} else {
				System.out.println("NIFTY 50 index category not found!");

			}

		} catch (IOException e) {
			System.err.println("Error reading file: " + e.getMessage());
		}
	}

	@Transactional
	public void mapStocksToIndexWithoutFile() {
		Optional<IndexCategory> nifty50IndexOptinal = indexCategoryRepository
				.findBySymbol("NSE:NIFTYMICROCAP250-INDEX");
		if (nifty50IndexOptinal.isPresent()) {
			IndexCategory nifty50Index = nifty50IndexOptinal.get();

			Set<StockMaster> nifty50Stocks = new HashSet<>();
			List<String> symbols = Arrays.asList("VMART", "SUPRIYA", "VENUSPIPES", "CSBBANK", "KSCL", "TARC", "SUNFLAG",
					"REFEX", "LLOYDSENT", "HIKAL", "SHOPERSTOP", "FORCEMOT", "RBA", "AVALON", "PGIL", "MAXESTATES",
					"AWFIS", "SHRIPISTON", "CARTRADE", "BALUFORGE", "JINDWORLD", "TIPSMUSIC", "KRBL", "VESUVIUS",
					"HERITGFOOD", "PNGJL", "INDIAGLYCO", "GARFIBRES", "RAIN", "KSL", "ACI", "BOROLTD", "ARVIND",
					"MANINFRA", "BBL", "FINEORG", "EPIGRAL", "GANESHHOUC", "KPIGREEN", "WABAG", "POLYPLEX", "VENTIVE",
					"VAIBHAVGBL", "SANSERA", "BANCOINDIA", "LUXIND", "RELIGARE", "MAHSCOOTER", "CEIGALL", "EIEL",
					"WONDERLA", "ORISSAMINE", "AVANTIFEED", "ABDL", "GHCL", "JCHAC", "SENCO", "ORIENTCEM", "TEXRAIL",
					"SYMPHONY", "THANGAMAYL", "ZYDUSWELL", "GRINFRA", "YATHARTH", "DHANI", "INGERRAND", "PTC", "NOCIL",
					"PCJEWELLER", "BOMDYEING", "JKPAPER", "NAZARA", "RESPONIND", "SHAKTIPUMP", "BAJAJHIND", "DODLA",
					"GOKEX", "SKIPPER", "PATELENG", "JKIL", "BORORENEW", "SULA", "SEQUENT", "BECTORFOOD", "INDIGOPNTS",
					"MSTCLTD", "ETHOSLTD", "MOIL", "PAISALO", "GMMPFAUDLR", "EMBDL", "PARAS", "FIEMIND", "GMRP&UI",
					"EDELWEISS", "PARADEEP", "MARKSANS", "MAHLIFE", "ROSSARI", "RAJESHEXPO", "RELINFRA", "ICIL",
					"SUNTECK", "CHEMPLASTS", "AVL", "ORCHPHARMA", "LXCHEM", "VIPIND", "RALLIS", "TIIL", "SANOFI",
					"BEPL", "GANECOS", "RATEGAIN", "SUBROS", "KTKBANK", "GSFC", "HEMIPROP", "IONEXCHANG", "GULFOILLUB",
					"ENTERO", "SHAREINDIA", "ARVINDFASN", "EMUDHRA", "GOPAL", "LLOYDSENGG", "ISGEC", "GREAVESCOT",
					"DCBBANK", "CHOICEIN", "GREENPLY", "NEOGEN", "TI", "HCC", "TDPOWERSYS", "VSTIND", "STAR", "VARROC",
					"AZAD", "NESCO", "INDIASHLTR", "SAFARI", "GAEL", "MIDHANI", "INFIBEAM", "CMSINFO", "EASEMYTRIP",
					"ITDCEM", "HEIDELBERG", "RTNPOWER", "INNOVACAP", "DCAL", "GABRIEL", "SANOFICONR", "REDTAPE",
					"POWERMECH", "SURYAROSNI", "TEGA", "IXIGO", "BALAMINES", "EMIL", "TRANSRAILL", "SOUTHBANK",
					"WELENT", "FCL", "STARCEMENT", "TIMETECHNO", "CIGNITITEC", "KSB", "AARTIPHARM", "DCXINDIA", "NFL",
					"SUPRAJIT", "VOLTAMP", "ELECTCAST", "FDC", "SFL", "HATHWAY", "ZAGGLE", "OPTIEMUS", "ADVENZYMES",
					"DATAMATICS", "BIRLACORPN", "ANUP", "KIRLPNU", "HCG", "CELLO", "IMFA", "SPANDANA", "IMAGICAA",
					"IFBIND", "EUREKAFORB", "ASHOKA", "AARTIDRUGS", "MTARTECH", "EQUITASBNK", "TIRUMALCHM", "JAIBALAJI",
					"PRICOLLTD", "SHARDACROP", "IIFLCAPS", "AGI", "GREENPANEL", "GATEWAY", "JKLAKSHMI", "THOMASCOOK",
					"SPARC", "JAMNAAUTO", "PRINCEPIPE", "HGINFRA", "AHLUCONT", "AKZOINDIA", "JAICORPLTD", "MEDPLUS",
					"SUDARSCHEM", "TEAMLEASE", "TVSSCS", "DBL", "UJJIVANSFB", "ASTRAMICRO", "SBCL", "PARKHOTELS",
					"DYNAMATECH", "BANSALWIRE", "ASKAUTOLTD", "AURIONPRO", "STLTECH", "AETHER", "STYLAMIND",
					"GRWRHITECH", "SHILPAMED", "SAMHI", "CIEINDIA", "NUVOCO", "AMIORG", "UNIMECH", "JSFB", "PRSMJOHNSN",
					"CYIENTDLM", "JISLJALEQS", "JTLIND", "ALLCARGO", "INOXGREEN", "EPL", "PRUDENT", "LMW", "BLACKBUCK",
					"WEBELSOLAR", "SHAILY", "BLUEJET");

			for (String symbol : symbols) {
				// Find stock by symbol and ISIN code
				Optional<StockMaster> stockOpt = stockMasterRepository
						.findBySymbol("NSE:".concat(symbol.trim()).concat("-EQ"));
				if (stockOpt.isPresent()) {
					nifty50Stocks.add(stockOpt.get());
				} else {
					System.out.println("Stock not found in DB: " + symbol);
				}

			}
			nifty50Index.setStocks(nifty50Stocks);
			indexCategoryRepository.save(nifty50Index);

			System.out.println("✅ NIFTY 50 stocks mapped successfully!");
		} else {
			System.out.println("NIFTY 50 index category not found!");

		}

	}

	public void clculateAndSaveTechnicalIndicator() {
		// List<StockMaster> stocks =
		// stockMasterRepository.findAllStocksWithoutIndicatorsNative();

		/*
		 * for (StockMaster stock : stocks) {
		 * calculationUtil.calculateAndSaveIndicators(stock);
		 * 
		 * }
		 */

		List<String> myList = Arrays.asList("3", "947", "1003", "1290", "1305", "1586", "2060", "15", "466", "961",
				"1147", "1357", "1567", "1770", "1771", "332", "204");

		// Example: print all items
		for (String item : myList) {
			Optional<StockMaster> stockOpt = stockMasterRepository.findById(Long.parseLong(item));
			if (stockOpt.isPresent()) {

				calculationUtil.calculateAndSaveIndicators(stockOpt.get());
			}

		}

	}

	public void updateStockPriceDaily() {
		List<StockMaster> allStocks = stockMasterRepository.findAll();

		LocalDate today = LocalTime.now().isAfter(LocalTime.of(16, 0)) ? LocalDate.now() : LocalDate.now().minusDays(1);

		LocalDate lastDate = stockDailyPriceRepository.findLatestTradeDate();
		long totalRecords = stockDailyPriceRepository.countByTradeDate(lastDate);

		if (totalRecords > (allStocks.size() / 2)) {
			LocalDate startDate = lastDate.plusDays(1);

			if (!startDate.isAfter(today)) {
				for (LocalDate current = startDate; !current.isAfter(today); current = current.plusDays(1)) {
					try {
						Path csvPath = bhavcopyDownloader.downloadCSV(current);
						List<BhavcopyEntry> entries = bhavcopyDownloader.parseFullBhavcopyCsv(csvPath.toString());

						if (entries.isEmpty()) {
							System.out.println("No data in Bhavcopy for " + current);
							continue;
						}

						LocalDate fileTradeDate = entries.get(0).getTradeDate();
						if (!fileTradeDate.equals(current)) {
							System.out.println("Mismatch date in file: " + fileTradeDate + " != " + current);
							continue;
						}

						updateStockFromDate(allStocks, entries, current);

						System.out.println("Updated stocks for " + current);
					} catch (Exception e) {
						System.err.println("Failed to update for date " + current + ": " + e.getMessage());
					}
				}
			} else {
				System.out.println("No new data to update. Already up to date till: " + lastDate);
			}
		} else {
			System.out.println("Last date (" + lastDate + ") seems incomplete, skipping update.");
		}
	}

	@Transactional
	public void updateStockFromDate(List<StockMaster> allStocks, List<BhavcopyEntry> entries, LocalDate currentDate) {
		try {
			// Map Bhavcopy entries by symbol (direct lookup O(1))
			Map<String, BhavcopyEntry> entryMap = entries.stream()
					.collect(Collectors.toMap(BhavcopyEntry::getSymbol, Function.identity()));

			// 2. Delete all existing records for this date in ONE query
			stockDailyPriceRepository.deleteByTradeDate(currentDate);
			indicatorRepository.deleteByTradeDate(currentDate);

			List<StockTechnicalIndicator> indicators = indicatorRepository
					.findLatestByTradeDateBefore(allStocks.stream().map(StockMaster::getId).toList(), currentDate);

			// Step 3: Convert to Map<stockId, indicator> for O(1) lookup
			Map<Long, StockTechnicalIndicator> latestIndicators = indicators.stream()
					.collect(Collectors.toMap(ind -> ind.getStockDailyPrice().getStock().getId(), Function.identity()));

			List<StockDailyPrice> dailyPrices = new ArrayList<>();
			List<StockTechnicalIndicator> udatedIndicators = new ArrayList<>();

			for (StockMaster stock : allStocks) {
				BhavcopyEntry entry = entryMap.get(stock.getSymbol());
				if (entry == null)
					continue; // skip stocks not present in Bhavcopy

				// Always overwrite existing data for the same date
				StockDailyPrice price = new StockDailyPrice();
				price.setStock(stock);
				price.setTradeDate(currentDate);
				price.setOpenPrice(entry.getOpenPrice());
				price.setHighPrice(entry.getHighPrice());
				price.setLowPrice(entry.getLowPrice());
				price.setClosePrice(entry.getClosePrice());
				price.setVolume((double) entry.getTotalTradedQty());
				price.setDeliveryPercent(entry.getDeliveryPercent());

				// latest indicator (can also batch optimize later if needed)
				StockTechnicalIndicator latestIndicator = latestIndicators.get(stock.getId());

				StockTechnicalIndicator indicator = helperClassForCalculation.calculateIndicators(price,
						latestIndicator);
				price.setTechnicalIndicator(indicator);

				dailyPrices.add(price);
				udatedIndicators.add(indicator);
			}

			stockDailyPriceRepository.saveAll(dailyPrices);
			indicatorRepository.saveAll(indicators);

			System.out.println("Updated " + dailyPrices.size() + " stocks for " + currentDate);

		} catch (Exception e) {
			System.err.println("Failed to update data on " + currentDate + " : " + e.getMessage());
			e.printStackTrace();
		}
	}

	@Transactional
	public void fetchAndSaveNifty5MinLatestCandle() {
		try {
			// 1. Get last saved candle
			Optional<Nifty5MinCandle> lastSavedOpt = niftyFiveMinCandleRepository.findTop1ByOrderByTimestampDesc();

			LocalDateTime startTime;
			if (lastSavedOpt.isPresent()) {
				startTime = lastSavedOpt.get().getTimestamp().plusMinutes(5);
			} else {
				// No data yet → start from default (market open today 9:15)
				LocalDate today = LocalDate.now(ZoneId.of("Asia/Kolkata"));
				startTime = LocalDateTime.of(today, LocalTime.of(9, 15));
			}

			// 2. Compute last fully completed candle
			LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Kolkata"));
			int minute = now.getMinute();
			int completedCandleMinute = (minute / 5) * 5 - 5; // last full 5-min candle
			if (completedCandleMinute < 0)
				completedCandleMinute = 55; // handle hour boundary

			LocalDateTime lastCompletedCandle = now.withMinute(completedCandleMinute).withSecond(0).withNano(0);

			// Prevent requesting future candles
			if (startTime.isAfter(lastCompletedCandle)) {
				return;
			}

			// 3. Prepare request for candles from startTime to lastCompletedCandle
			StockHistoryModel model = helperClassForCalculation.prepareRequestModel("NSE:NIFTY50-INDEX", "5MIN",
					startTime.toLocalDate(), lastCompletedCandle.toLocalDate());

			List<StockCandle> candles = getHistoricalData(model);

			if (!candles.isEmpty()) {

				List<StockCandle> completedCandles;

				LocalTime currentTime = LocalDateTime.now().toLocalTime();

				if (currentTime.compareTo(LocalTime.of(15, 30)) > 0) {
					completedCandles = candles;

				} else {
					completedCandles = candles.size() > 1 ? candles.subList(0, candles.size() - 1)
							: Collections.emptyList();

				}

				List<Nifty5MinCandle> entities = completedCandles.stream().map(c -> {
					LocalDateTime ts = LocalDateTime.ofInstant(c.getTimestamp(), ZoneId.of("Asia/Kolkata"));
					return new Nifty5MinCandle(c.getOpen(), c.getHigh(), c.getLow(), c.getClose(), ts);
				}).filter(c -> !niftyFiveMinCandleRepository.existsByTimestamp(c.getTimestamp()))
						.collect(Collectors.toList());

				if (!entities.isEmpty()) {
					niftyFiveMinCandleRepository.saveAll(entities);
					updateEmaForNewCandles();
					//niftyEmaCrossoverService.runIntradayStrategy();

				}
			}

		} catch (Exception e) {
			System.err.println("Error in fetchAndSaveLatestCandle: " + e.getMessage());
			e.printStackTrace();
		}
	}

	@Transactional
	public void fetchAndSaveStock5MinLatestCandle(String symbol) {
		try {
			// 1️⃣ Get last saved candle for this symbol
			Optional<FNO5MinCandle> lastSavedOpt = fno5MinCandleRepository.findTop1BySymbolOrderByTimestampDesc(symbol);

			LocalDateTime startTime;
			if (lastSavedOpt.isPresent()) {
				startTime = lastSavedOpt.get().getTimestamp().plusMinutes(5);
			} else {
				LocalDate today = LocalDate.now(ZoneId.of("Asia/Kolkata"));
				startTime = LocalDateTime.of(today, LocalTime.of(9, 15));
			}

			// 2️⃣ Compute last fully completed candle
			LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Kolkata"));
			int minute = now.getMinute();
			int completedCandleMinute = (minute / 5) * 5 - 5; // last full 5-min candle
			if (completedCandleMinute < 0)
				completedCandleMinute = 55;

			LocalDateTime lastCompletedCandle = now.withMinute(completedCandleMinute).withSecond(0).withNano(0);

			if (startTime.isAfter(lastCompletedCandle)) {
				System.out.println(
						"⏩ No new completed candle for " + symbol + " (Last saved: " + startTime.minusMinutes(5) + ")");
				return;
			}

			// 3️⃣ Prepare API request
			StockHistoryModel model = helperClassForCalculation.prepareRequestModel(symbol, "5MIN",
					startTime.toLocalDate(), lastCompletedCandle.toLocalDate());

			List<StockCandle> candles = getHistoricalData(model);

			if (candles.isEmpty()) {
				return;
			}

			// 4️⃣ Keep only completed candles (skip current forming one)
			LocalTime currentTime = now.toLocalTime();
			List<StockCandle> completedCandles = (currentTime.isAfter(LocalTime.of(15, 30))) ? candles
					: candles.size() > 1 ? candles.subList(0, candles.size() - 1) : Collections.emptyList();

			if (completedCandles.isEmpty()) {
				System.out.println("No completed candles to save for " + symbol);
				return;
			}

			// 5️⃣ Convert to entity and filter duplicates
			List<FNO5MinCandle> entities = completedCandles.stream().map(c -> {
				LocalDateTime ts = LocalDateTime.ofInstant(c.getTimestamp(), ZoneId.of("Asia/Kolkata"));
				return new FNO5MinCandle(symbol, ts, c.getOpen(), c.getHigh(), c.getLow(), c.getClose(), c.getVolume());
			}).filter(c -> !fno5MinCandleRepository.existsBySymbolAndTimestamp(symbol, c.getTimestamp()))
					.collect(Collectors.toList());

			// 6️⃣ Save candles
			if (!entities.isEmpty()) {
				fno5MinCandleRepository.saveAll(entities);
				System.out.printf("✅ Saved %d new 5-min candles for %s. Last: %s%n", entities.size(), symbol,
						entities.get(entities.size() - 1).getTimestamp());
			} else {
				System.out.println("All fetched candles already exist for " + symbol);
			}

		} catch (Exception e) {
			System.err.println("⚠️ Error fetching 5-min candles for " + symbol + ": " + e.getMessage());
			e.printStackTrace();
		}
	}

	@Transactional
	public void updateEmaForNewCandles() {
		// 1. Find last candle with EMA already calculated
		Nifty5MinCandle lastWithEma = niftyFiveMinCandleRepository
				.findTopByEma9IsNotNullAndEma21IsNotNullOrderByTimestampDesc();

		if (lastWithEma == null) {
			System.out.println("No candle with EMA found. Run full recalculation once.");
			return;
		}

		// 2. Get new candles after last timestamp
		List<Nifty5MinCandle> newCandles = niftyFiveMinCandleRepository
				.findByTimestampAfterOrderByTimestampAsc(lastWithEma.getTimestamp());

		if (newCandles.isEmpty()) {
			System.out.println("No new candles to update.");
			return;
		}

		// 3. Start from last EMA values
		double ema9 = lastWithEma.getEma9();
		double ema21 = lastWithEma.getEma21();

		double multiplier9 = 2.0 / (9 + 1);
		double multiplier21 = 2.0 / (21 + 1);

		// 4. Compute incrementally for new candles
		for (Nifty5MinCandle candle : newCandles) {
			double close = candle.getClose();

			ema9 = ((close - ema9) * multiplier9) + ema9;
			ema21 = ((close - ema21) * multiplier21) + ema21;

			candle.setEma9(TradingUtil.roundToTwoDecimalPlaces(ema9));
			candle.setEma21(TradingUtil.roundToTwoDecimalPlaces(ema21));
		}

		// 5. Save updated candles
		niftyFiveMinCandleRepository.saveAll(newCandles);

	}

	public void fetchAndSave5MinCandlesForAllFNO(List<String> symbols) {
		for (String symbol : symbols) {
			fetchAndSave5MinCandlesForFNO(symbol);
		}
	}

	@Transactional
	public void fetchAndSave5MinCandlesForFNO(String symbol) {
		ZoneId zone = ZoneId.of("Asia/Kolkata");
		LocalTime marketOpen = LocalTime.of(9, 15);
		LocalTime marketClose = LocalTime.of(15, 30);
		LocalDate defaultStartDate = LocalDate.of(2025, 9, 25);

		try {
			// 1️⃣ Determine start time
			Optional<FNO5MinCandle> lastSavedOpt = fno5MinCandleRepository.findTop1BySymbolOrderByTimestampDesc(symbol);
			LocalDateTime startTime = lastSavedOpt.map(c -> c.getTimestamp().plusMinutes(5))
					.orElse(LocalDateTime.of(defaultStartDate, marketOpen));

			// 2️⃣ Determine last completed candle time
			LocalDateTime now = LocalDateTime.now(zone);
			int completedCandleMinute = ((now.getMinute() / 5) * 5) - 5;
			if (completedCandleMinute < 0)
				completedCandleMinute = 55;

			LocalDateTime lastCompletedCandle = now.withMinute(completedCandleMinute).withSecond(0).withNano(0);

			// If no new completed candle yet
			if (startTime.isAfter(lastCompletedCandle)) {
				return;
			}

			// 3️⃣ Prepare request and fetch historical data
			StockHistoryModel model = helperClassForCalculation.prepareRequestModel(symbol, "5MIN",
					startTime.toLocalDate(), lastCompletedCandle.toLocalDate());

			List<StockCandle> candles = getHistoricalData(model);
			if (candles == null || candles.isEmpty()) {
				return;
			}

			// 4️⃣ Convert to entity
			List<FNO5MinCandle> entities = candles.stream().map(c -> {
				LocalDateTime ts = LocalDateTime.ofInstant(c.getTimestamp(), zone);
				FNO5MinCandle fnoCandle = new FNO5MinCandle();
				fnoCandle.setSymbol(symbol);
				fnoCandle.setOpen(c.getOpen());
				fnoCandle.setHigh(c.getHigh());
				fnoCandle.setLow(c.getLow());
				fnoCandle.setClose(c.getClose());
				fnoCandle.setVolume(c.getVolume());
				fnoCandle.setTimestamp(ts);
				return fnoCandle;
			}).collect(Collectors.toList());

			// 5️⃣ Remove candles already saved
			if (lastSavedOpt.isPresent()) {
				LocalDateTime lastSavedTime = lastSavedOpt.get().getTimestamp();
				entities = entities.stream().filter(c -> c.getTimestamp().isAfter(lastSavedTime)).collect(
						Collectors.collectingAndThen(Collectors.toMap(c -> c.getSymbol() + "_" + c.getTimestamp(),
								c -> c, (c1, c2) -> c1, LinkedHashMap::new), m -> new ArrayList<>(m.values())));
			}

			// 6️⃣ Remove incomplete last candle (if it’s still forming)
			if (!entities.isEmpty()) {
				FNO5MinCandle lastCandle = entities.get(entities.size() - 1);
				LocalDateTime lastCandleEnd = lastCandle.getTimestamp().plusMinutes(5);

				if (lastCandleEnd.isAfter(LocalDateTime.now(zone))) {
					entities.remove(entities.size() - 1);
				}
			}

			// 7️⃣ Save only if new completed candles exist
			if (!entities.isEmpty()) {
				fno5MinCandleRepository.saveAll(entities);
			}

		} catch (Exception e) {
			System.err.println("❌ Error fetching/saving candles for " + symbol + ": " + e.getMessage());
			e.printStackTrace();
		}
	}

}
