package com.kdt.instakyuram.security.jwt;

public class JwtRefreshTokenNotFoundException extends JwtTokenNotFoundException {
	public JwtRefreshTokenNotFoundException() {
		super();
	}

	public JwtRefreshTokenNotFoundException(String message) {
		super(message);
	}

	public JwtRefreshTokenNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}
}
