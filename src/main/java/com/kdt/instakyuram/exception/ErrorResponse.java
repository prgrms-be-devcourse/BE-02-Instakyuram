package com.kdt.instakyuram.exception;

public class ErrorResponse<T> {
	private final String errorCode;
	private final T response;

	public ErrorResponse(String errorCode, T response) {
		this.response = response;
		this.errorCode = errorCode;
	}
}
