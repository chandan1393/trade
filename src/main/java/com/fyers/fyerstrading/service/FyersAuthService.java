package com.fyers.fyerstrading.service;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fyers.fyerstrading.utility.Util;
import com.tts.in.model.FyersClass;

@Service
public class FyersAuthService {

	private static final String REFRESH_TOKEN_URL = "https://api-t1.fyers.in/api/v3/validate-refresh-token";

	
	public void getGenerateCode(String redirectURI, FyersClass fyersClass) {
	    fyersClass.GenerateCode(redirectURI);
	}
	
	
	public JSONObject generateAccessToken(String clientID, String clientSecret, String authCode,
			FyersClass fyersClass) {
		String appHashId = Util.computeAppIdHash(clientID, clientSecret);
		JSONObject jsonObject = fyersClass.GenerateToken(authCode, appHashId);
		System.out.println(jsonObject);
		return jsonObject;
	}

	public String generateAccessTokenUsingRefreshToken(String clientID, String clientSecret,
			String refreshToken) {

		try {
			RestTemplate restTemplate = new RestTemplate(); 
			String appHashId = Util.computeAppIdHash(clientID, clientSecret);
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

			Map<String, Object> requestBody = new HashMap<>();
			requestBody.put("grant_type", "refresh_token");
			requestBody.put("appIdHash", appHashId);
			requestBody.put("refresh_token", refreshToken);
			requestBody.put("pin", "1993"); // If PIN is required

			HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
			ResponseEntity<String> response = restTemplate.exchange(REFRESH_TOKEN_URL, HttpMethod.POST, entity,
					String.class);
			System.out.println("Response: " + response.getBody());
			String responseBody = response.getBody();
			JSONObject jsonResponse = new JSONObject(responseBody);

			if (jsonResponse.getInt("code") == 200) {
				String accessToken = jsonResponse.getString("access_token");
				return accessToken;
			} else {
				System.out.println("Failed to refresh token. Response: " + responseBody);
				return "";
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

}
