package com.kdt.instakyuram.common.file.exception;

public class FileReadException extends FileStorageException {

	protected FileReadException() {
	}

	public FileReadException(String message) {
		super(message);
	}

	public FileReadException(String message, Throwable cause) {
		super(message, cause);
	}

}
