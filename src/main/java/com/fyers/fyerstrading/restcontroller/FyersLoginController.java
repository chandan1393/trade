package com.fyers.fyerstrading.restcontroller;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fyers.fyerstrading.entity.FyersAuthDetails;
import com.fyers.fyerstrading.model.FyersSessionContext;
import com.fyers.fyerstrading.repo.FyersAuthRepository;
import com.fyers.fyerstrading.service.AuthService;
import com.fyers.fyerstrading.service.FyersApiService;
import com.fyers.fyerstrading.service.FyersAuthService;
import com.fyers.fyerstrading.service.FyersTokenManager;
import com.fyers.fyerstrading.websocket.MarketDataWebSocketService;
import com.tts.in.model.FyersClass;

@RestController
public class FyersLoginController {

	@Autowired
	private FyersAuthService fyersAuthService;

	@Autowired
	private FyersAuthRepository fyersAuthRepository;

	@Autowired
	private FyersSessionContext fyersSessionContext;

	@Autowired
	private MarketDataWebSocketService webSocketService;
	
	@Autowired
	private FyersApiService fyersApiService;
	
	@Autowired
	AuthService authService;

	@Autowired
	private FyersTokenManager tokenManager;

	@GetMapping("/loginToFyers")
	public void initiateLogin() {
		String redirectURI = "http://localhost:8080/callBackForFyers";

		FyersAuthDetails fyersAuthDetails = authService.fetchFyersAuthDetail();
		FyersClass fyersClass = FyersClass.getInstance();
		fyersClass.clientId = fyersAuthDetails.getClientId();
		fyersAuthService.getGenerateCode(redirectURI, fyersClass); 
	}

	@GetMapping("/callBackForFyers")
	public String getCallBackForFyers(@RequestParam("auth_code") String authCode) {
		try {
			FyersAuthDetails fyersAuthDetails = authService.fetchFyersAuthDetail();
			FyersClass fyersClass = FyersClass.getInstance();
			fyersClass.clientId = fyersAuthDetails.getClientId();

			JSONObject response = fyersAuthService.generateAccessToken(fyersAuthDetails.getClientId(),
					fyersAuthDetails.getClientSecret(), authCode, fyersClass);

			String accessToken = response.getString("access_token");
			String refreshToken = response.getString("refresh_token");

			// Update DB
			fyersAuthDetails.setAuthCode(authCode);
			fyersAuthDetails.setAccessToken(accessToken);
			fyersAuthDetails.setRefreshToken(refreshToken);
			fyersAuthDetails.setRefreshTokenCreatedAt(LocalDateTime.now());
			fyersAuthRepository.save(fyersAuthDetails);

			// Update runtime session
			fyersSessionContext.setAccessToken(accessToken);
			fyersSessionContext.setRefreshToken(refreshToken);
			fyersSessionContext.setAuthCode(authCode);
			fyersSessionContext.setClientId(fyersAuthDetails.getClientId());
			fyersApiService.setFyersToken(fyersSessionContext);

			// Optional: Start WebSocket if market is open
			if (isMarketOpenNow()) {
				webSocketService.startWebSocket();
			}

			return "Fyers login successful and access token saved.";
		} catch (Exception e) {
			e.printStackTrace();
			return "Fyers login failed: " + e.getMessage();
		}
	}

	private boolean isMarketOpenNow() {
		LocalTime now = LocalTime.now();
		DayOfWeek day = LocalDate.now().getDayOfWeek();
		return now.isAfter(LocalTime.of(9, 14)) && now.isBefore(LocalTime.of(15, 31))
				&& !(day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY);
	}
}
