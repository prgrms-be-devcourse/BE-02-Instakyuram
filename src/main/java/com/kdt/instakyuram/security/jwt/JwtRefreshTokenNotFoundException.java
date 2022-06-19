package com.kdt.instakyuram.security.jwt;

public class JwtRefreshTokenNotFoundException extends RuntimeException{

	public JwtRefreshTokenNotFoundException() {
	}

	public JwtRefreshTokenNotFoundException(String message) {
		super(message);
	}

	public JwtRefreshTokenNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}
}
