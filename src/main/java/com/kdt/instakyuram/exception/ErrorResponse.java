package com.kdt.instakyuram.exception;

public class ErrorResponse {
	private final String code;
	private final String message;

	public ErrorResponse(String code, String message) {
		this.code = code;
		this.message = message;
	}
}
