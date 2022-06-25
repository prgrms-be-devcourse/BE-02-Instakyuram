package com.kdt.instakyuram.common.file.exception;

public class FileWriteException extends FileStorageException {
	public FileWriteException() {
	}

	public FileWriteException(String message) {
		super(message);
	}

	public FileWriteException(String message, Throwable cause) {
		super(message, cause);
	}
}
