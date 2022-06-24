package com.kdt.instakyuram.exception.file;

import com.kdt.instakyuram.exception.ErrorCode;
import com.kdt.instakyuram.exception.file.FileException;

public class InvalidFileException extends FileException {
	private final ErrorCode errorCode = ErrorCode.INVALID_FILE;

	public InvalidFileException() {
	}

	public InvalidFileException(String message) {
		super(message);
	}

	public ErrorCode getErrorCode() {
		return errorCode;
	}
}
