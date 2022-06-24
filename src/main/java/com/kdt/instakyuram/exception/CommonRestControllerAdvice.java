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
	public ResponseEntity<ErrorResponse<String>> businessExceptionHandle(BusinessException e) {
		this.log.error(e.getMessage(), e);
		ErrorCode errorCode = e.errorCode();

		return new ResponseEntity<>(new ErrorResponse<>(errorCode.code(), errorCode.message()), errorCode.httpStatus());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorResponse<ValidationError> methodArgumentNotValidExceptionHandle(
		MethodArgumentNotValidException e) {
		this.log.debug(e.getMessage(), e);

		ValidationError validationError = e.getFieldErrors()
			.stream()
			.findFirst()
			.map(fieldError ->
				new ValidationError(
					fieldError.getField(),
					fieldError.getCode(),
					fieldError.getRejectedValue(),
					fieldError.getDefaultMessage())
			)
			.get();

		return new ErrorResponse<>(ErrorCode.METHOD_ARGUMENT_NOT_VALID.code(), validationError);
	}

	@ExceptionHandler({TransactionSystemException.class, ConstraintViolationException.class})
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ValidationError constraintViolationExceptionHandle(
		ConstraintViolationException e) {
		this.log.info(e.getMessage(), e);

		return e.getConstraintViolations().stream()
			.findFirst()
			.map(constraintViolation -> new ValidationError(
				constraintViolation.getPropertyPath().toString(),
				constraintViolation.getConstraintDescriptor()
					.getAnnotation()
					.annotationType()
					.getSimpleName(),
				constraintViolation.getInvalidValue(),
				constraintViolation.getMessage()))
			.get();
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ErrorResponse<String> unexpectedExceptionHandle(Exception e) {
		this.log.error(e.getMessage(), e);
		ErrorCode errorCode = ErrorCode.INTERNAL_SEVER_ERROR;

		return new ErrorResponse<>(errorCode.code(), errorCode.message());
	}
}
