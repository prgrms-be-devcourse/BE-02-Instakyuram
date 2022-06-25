package com.kdt.instakyuram.exception;

public class DomainException extends RuntimeException {
	private final ErrorCode errorCode;

	protected DomainException(ErrorCode errorCode, String message) {
		super(message);
		this.errorCode = errorCode;
	}

	public ErrorCode getErrorCode() {
		return errorCode;
	}
}
