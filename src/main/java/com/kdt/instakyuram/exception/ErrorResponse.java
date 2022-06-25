package com.kdt.instakyuram.exception;

public class ErrorResponse<T extends ErrorCode> {
	private final String code;
	private final String message;

	public ErrorResponse(T errorCode) {
		this.code = errorCode.getCode();
		this.message = errorCode.getMessage();
	}

	public String getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}
}
