package com.kdt.instakyuram.security.jwt;

public class JwtTokenNotFoundException extends RuntimeException {
	public JwtTokenNotFoundException() {
	}

	public JwtTokenNotFoundException(String message) {
		super(message);
	}

	public JwtTokenNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}
}
