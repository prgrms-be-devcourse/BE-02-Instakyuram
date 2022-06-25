package com.kdt.instakyuram.exception;

import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.apache.tomcat.util.http.fileupload.impl.SizeLimitExceededException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.kdt.instakyuram.common.file.exception.FileStorageException;

@RestControllerAdvice
public class CommonRestControllerAdvice {

	private static final Logger log = LoggerFactory.getLogger(CommonRestControllerAdvice.class);

	@ExceptionHandler({SizeLimitExceededException.class, FileSizeLimitExceededException.class})
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorResponse invalidFileException(Exception e) {
		log.warn("ERROR-XXX : {}", e.getMessage(), e);

		return toErrorResponse("9999", e.getMessage());
	}

	@ExceptionHandler(FileStorageException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ErrorResponse handleFileStorageException(FileStorageException e) {
		log.warn("ERROR-XXX : {}", e.getMessage(), e);

		return toErrorResponse("1004", e.getMessage());
	}

	public ErrorResponse toErrorResponse(String code, String message) {
		return new ErrorResponse(code, message);
	}

}
