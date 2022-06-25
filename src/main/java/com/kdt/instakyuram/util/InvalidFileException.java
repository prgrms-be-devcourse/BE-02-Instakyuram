package com.kdt.instakyuram.util;

public class InvalidFileException extends RuntimeException {
	public InvalidFileException() {
	}

	public InvalidFileException(String message) {
		super(message);
	}

	public InvalidFileException(String message, Throwable cause) {
		super(message, cause);
	}
}
