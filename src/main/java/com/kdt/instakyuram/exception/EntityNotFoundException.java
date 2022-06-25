package com.kdt.instakyuram.exception;

public class EntityNotFoundException extends BusinessException {

	public EntityNotFoundException(ErrorCode errorCode) {
		super(errorCode);
	}

	public EntityNotFoundException(ErrorCode errorCode, String message) {
		super(errorCode, message);

	}

	public EntityNotFoundException(ErrorCode errorCode, String message, Throwable cause) {
		super(errorCode, message, cause);
	}

	@Override
	public String toString() {
		return "EntityNotFoundException - " + errorCode();
	}
}
