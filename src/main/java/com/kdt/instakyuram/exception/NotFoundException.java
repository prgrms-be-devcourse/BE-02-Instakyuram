package com.kdt.instakyuram.exception;

public class NotFoundException extends RuntimeException {

	private final ErrorCode errorCode;

	public NotFoundException(ErrorCode errorCode) {
		this.errorCode = errorCode;
	}

	public NotFoundException(ErrorCode errorCode, String message) {
		super(message);
		this.errorCode = errorCode;
	}

	public NotFoundException(ErrorCode errorCode, String message, Throwable cause) {
		super(message, cause);
		this.errorCode = errorCode;
	}

	public ErrorCode errorCode() {
		return errorCode;
	}
}
