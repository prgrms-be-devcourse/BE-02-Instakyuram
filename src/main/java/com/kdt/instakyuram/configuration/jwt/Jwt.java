package com.kdt.instakyuram.configuration.jwt;

public class Jwt {
	private final String issuer;
	private final String clientSecret;
	private final int expirySeconds;

	public Jwt(String issuer, String clientSecret, int expirySeconds) {
		this.issuer = issuer;
		this.clientSecret = clientSecret;
		this.expirySeconds = expirySeconds;
	}

}
