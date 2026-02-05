package com.fyers.fyerstrading.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fyers.fyerstrading.entity.FyersAuthDetails;
import com.fyers.fyerstrading.model.FyersProperties;
import com.fyers.fyerstrading.model.FyersSessionContext;
import com.fyers.fyerstrading.repo.FyersAuthRepository;

@Service
public class FyersTokenManager {

    @Autowired
    private FyersAuthRepository fyersAuthRepository;

    @Autowired
    private FyersAuthService fyersAuthService;

    @Autowired
    private FyersProperties fyersProperties;

    @Autowired
    private FyersSessionContext fyersSessionContext;
    
    @Autowired
    private FyersApiService fyersApiService;

	public void refreshAccessTokenOnStartup() {
		Optional<FyersAuthDetails> optional = fyersAuthRepository.findAll().stream().findFirst();

		if (optional.isEmpty()) {
			System.err.println("No FyersAuthDetails found in DB.");
			return;
		}

		FyersAuthDetails auth = optional.get();

		if (auth.getRefreshToken() == null || auth.getRefreshToken().isEmpty()) {
			System.err.println("Missing refresh token.");
			return;
		}

		try {
			String accessToken = fyersAuthService.generateAccessTokenUsingRefreshToken(auth.getClientId(),
					auth.getClientSecret(), auth.getRefreshToken());

			auth.setAccessToken(accessToken);
			fyersAuthRepository.save(auth);

			fyersSessionContext.setAccessToken(accessToken);
			fyersSessionContext.setRefreshToken(auth.getRefreshToken());
			fyersSessionContext.setAuthCode(auth.getAuthCode());
			fyersSessionContext.setClientId(auth.getClientId());
			
			fyersApiService.setFyersToken(fyersSessionContext);
			
		} catch (Exception e) {
			System.err.println("Error refreshing Fyers token: " + e.getMessage());
		}
	}

    
}

