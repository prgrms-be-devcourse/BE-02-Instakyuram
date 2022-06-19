package com.kdt.instakyuram.security.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConstructorBinding
@ConfigurationProperties(prefix = "jwt")
public record JwtConfigure(
	AccessTokenProperties accessToken,
	RefreshTokenProperties refreshToken,
	String issuer,
	String clientSecret
) {
	public JwtConfigure(AccessTokenProperties accessToken, RefreshTokenProperties refreshToken, String issuer,
		String clientSecret) {
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
		this.issuer = issuer;
		this.clientSecret = clientSecret;
	}

	public record AccessTokenProperties(String header, int expirySeconds) {

	}

	public record RefreshTokenProperties(String header, int expirySeconds) {

	}
}
