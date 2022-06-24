package com.kdt.instakyuram.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CommonRestControllerAdvice {

	private static final Logger log = LoggerFactory.getLogger(CommonRestControllerAdvice.class);

	@ResponseStatus(code = HttpStatus.BAD_REQUEST)
	@ExceptionHandler(DomainException.class)
	public ErrorResponse handleDomainException(DomainException e) {
		ErrorCode errorCode = e.getErrorCode();
		log.warn("ERROR-{} : {}", errorCode.getCode(), errorCode.getDescription(), e);
		return new ErrorResponse(errorCode.getCode(), e.getMessage());
	}

	@ResponseStatus(code = HttpStatus.BAD_REQUEST)
	@ExceptionHandler(DataIntegrityViolationException.class)
	public ErrorResponse handleDataIntegrityViolationException(DataIntegrityViolationException e) {
		log.warn("ERROR-100 - {} : {}", e, e);
		return new ErrorResponse("100", e.getMessage());
	}
}
