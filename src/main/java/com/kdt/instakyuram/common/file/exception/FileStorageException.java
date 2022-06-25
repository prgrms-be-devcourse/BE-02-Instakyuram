package com.kdt.instakyuram.common.file.exception;

public class FileStorageException extends RuntimeException {
	public FileStorageException() {
	}

	public FileStorageException(String message) {
		super(message);
	}

	public FileStorageException(String message, Throwable cause) {
		super(message, cause);
	}
}
