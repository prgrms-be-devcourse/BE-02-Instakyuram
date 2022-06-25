package com.kdt.instakyuram.common.file.exception;

public class InvalidFileException extends FileWriteException {
	public InvalidFileException() {
	}

	public InvalidFileException(String message) {
		super(message);
	}

	public InvalidFileException(String message, Throwable cause) {
		super(message, cause);
	}
}
