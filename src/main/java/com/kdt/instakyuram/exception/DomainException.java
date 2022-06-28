package com.kdt.instakyuram.exception;

public class DomainException extends RuntimeException {
	private final ErrorCode errorCode;

	public DomainException(String message) {
		super(message);
		this.errorCode = ErrorCode.DOMAIN_EXCEPTION;
	}

	public ErrorCode getErrorCode() {
		return errorCode;
	}
}
