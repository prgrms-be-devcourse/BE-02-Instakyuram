package com.kdt.instakyuram.exception.file;

import com.kdt.instakyuram.exception.ErrorCode;

public class FileException extends RuntimeException{
	public FileException() {
	}

	public FileException(String message) {
		super(message);
	}

	public FileException(String message, Throwable cause) {
		super(message, cause);
	}
}
