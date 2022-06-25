package com.kdt.instakyuram.exception;

import javax.validation.ConstraintViolationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CommonRestControllerAdvice {

	private final Logger log = LoggerFactory.getLogger(getClass());

	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<ErrorResponse<ErrorCode>> businessExceptionHandle(BusinessException e) {
		this.log.info("{}", e.toString(), e);
		ErrorCode errorCode = e.errorCode();

		return new ResponseEntity<>(new ErrorResponse<>(errorCode), errorCode.getStatus());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorResponse<ErrorCode> methodArgumentNotValidExceptionHandle(MethodArgumentNotValidException e) {
		this.log.warn(e.getMessage(), e);

		return new ErrorResponse<>(ErrorCode.METHOD_ARGUMENT_NOT_VALID);
	}

	@ExceptionHandler({TransactionSystemException.class, ConstraintViolationException.class})
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorResponse<ErrorCode> constraintViolationExceptionHandle(ConstraintViolationException e) {
		this.log.warn(e.getMessage(), e);

		return new ErrorResponse<>(ErrorCode.CONSTRAINT_VIOLATION);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse<ErrorCode>> unexpectedExceptionHandle(Exception e) {
		this.log.warn(e.getMessage(), e);
		ErrorCode errorCode = ErrorCode.INTERNAL_SEVER_ERROR;

		return new ResponseEntity<>(new ErrorResponse<>(errorCode), errorCode.getStatus());
	}
}
