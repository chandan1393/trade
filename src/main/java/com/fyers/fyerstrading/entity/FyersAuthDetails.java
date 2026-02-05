package com.fyers.fyerstrading.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "fyers_auth_details")
public class FyersAuthDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "client_id", nullable = false, unique = true)
	private String clientId;

	@Column(name = "access_token")
	private String accessToken;

	@Column(name = "refresh_token")
	private String refreshToken;

	@Column(name = "auth_code")
	private String authCode;

	@Column(name = "refresh_token_created_at")
	private LocalDateTime refreshTokenCreatedAt;

	@Column(name = "expires_in")
	private Long expiresIn; // Token expiry duration in seconds

	@Column(name = "client_secret")
	private String clientSecret;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public String getAuthCode() {
		return authCode;
	}

	public void setAuthCode(String authCode) {
		this.authCode = authCode;
	}

	public LocalDateTime getRefreshTokenCreatedAt() {
		return refreshTokenCreatedAt;
	}

	public void setRefreshTokenCreatedAt(LocalDateTime refreshTokenCreatedAt) {
		this.refreshTokenCreatedAt = refreshTokenCreatedAt;
	}

	public Long getExpiresIn() {
		return expiresIn;
	}

	public void setExpiresIn(Long expiresIn) {
		this.expiresIn = expiresIn;
	}

	public String getClientSecret() {
		return clientSecret;
	}

	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}

}
