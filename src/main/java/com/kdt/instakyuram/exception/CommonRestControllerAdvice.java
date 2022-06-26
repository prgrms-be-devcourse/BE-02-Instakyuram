package com.kdt.instakyuram.exception;

import javax.validation.ConstraintViolationException;

import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.apache.tomcat.util.http.fileupload.impl.SizeLimitExceededException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.kdt.instakyuram.common.file.exception.FileStorageException;

@RestControllerAdvice
public class CommonRestControllerAdvice {

	private final Logger log = LoggerFactory.getLogger(getClass());

	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<ErrorResponse<ErrorCode>> handleBusinessException(BusinessException e) {
		this.log.info("{}", e.toString(), e);
		ErrorCode errorCode = e.errorCode();

		return new ResponseEntity<>(new ErrorResponse<>(errorCode), errorCode.getStatus());
	}

	@ExceptionHandler(BindException.class)
	public ResponseEntity<ErrorResponse<ErrorCode>> handleBindException(BindException e) {
		this.log.warn(e.getMessage(), e);
		ErrorCode errorCode = ErrorCode.BIND_ERROR;

		return new ResponseEntity<>(new ErrorResponse<>(errorCode), errorCode.getStatus());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse<ErrorCode>> handleMethodArgumentNotValidException(
		MethodArgumentNotValidException e) {
		this.log.warn(e.getMessage(), e);
		ErrorCode errorCode = ErrorCode.METHOD_ARGUMENT_NOT_VALID;

		return new ResponseEntity<>(new ErrorResponse<>(errorCode), errorCode.getStatus());
	}

	@ExceptionHandler({TransactionSystemException.class, ConstraintViolationException.class})
	public ResponseEntity<ErrorResponse<ErrorCode>> handleConstraintViolation(ConstraintViolationException e) {
		this.log.warn(e.getMessage(), e);
		ErrorCode errorCode = ErrorCode.METHOD_ARGUMENT_NOT_VALID;

		return new ResponseEntity<>(new ErrorResponse<>(errorCode), errorCode.getStatus());
	}

	@ExceptionHandler(SizeLimitExceededException.class)
	public ResponseEntity<ErrorResponse<ErrorCode>> handleSizeLimitExceededException(Exception e) {
		this.log.warn("{}", e.toString(), e);
		ErrorCode errorCode = ErrorCode.SIZE_LIMIT_EXCEEDED;

		return new ResponseEntity<>(new ErrorResponse<>(errorCode), errorCode.getStatus());
	}

	@ExceptionHandler(FileSizeLimitExceededException.class)
	public ResponseEntity<ErrorResponse<ErrorCode>> handleFileSizeLimitExceededException(Exception e) {
		this.log.warn("{}", e.toString(), e);
		ErrorCode errorCode = ErrorCode.FILE_SIZE_LIMIT_EXCEEDED;

		return new ResponseEntity<>(new ErrorResponse<>(errorCode), errorCode.getStatus());
	}

	@ExceptionHandler(FileStorageException.class)
	public ResponseEntity<ErrorResponse<ErrorCode>> handleFileStorageException(FileStorageException e) {
		this.log.warn("{}", e.toString(), e);
		ErrorCode errorCode = ErrorCode.FILE_STORAGE_EXCEPTION;

		return new ResponseEntity<>(new ErrorResponse<>(errorCode), errorCode.getStatus());
	}

	@ExceptionHandler(DomainException.class)
	public ResponseEntity<ErrorResponse<ErrorCode>> handleDomainException(DomainException e) {
		this.log.warn("{}", e.toString(), e);
		ErrorCode errorCode = e.getErrorCode();

		return new ResponseEntity<>(new ErrorResponse<>(errorCode), errorCode.getStatus());
	}

	@ResponseStatus(code = HttpStatus.BAD_REQUEST)
	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<ErrorResponse<ErrorCode>> handleDataIntegrityViolationException(
		DataIntegrityViolationException e) {
		this.log.warn("ERROR-100 - {} : {}", e, e);
		ErrorCode errorCode = ErrorCode.DATA_INTEGRITY_VIOLATION;

		return new ResponseEntity<>(new ErrorResponse<>(errorCode), errorCode.getStatus());
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse<ErrorCode>> handleUnExpectedException(Exception e) {
		this.log.warn(e.getMessage(), e);
		ErrorCode errorCode = ErrorCode.INTERNAL_SEVER_ERROR;

		return new ResponseEntity<>(new ErrorResponse<>(errorCode), errorCode.getStatus());
	}
}
