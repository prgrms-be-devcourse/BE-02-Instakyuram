package com.kdt.instakyuram.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
	//COMMON
	INTERNAL_SEVER_ERROR("C001", "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR),
	NOT_FOUND_EXCEPTION("C0002", "Not found exception", HttpStatus.NOT_FOUND),
	BIND_ERROR("C0003", "Binding Exception", HttpStatus.BAD_REQUEST),
	//VALIDATION
	METHOD_ARGUMENT_NOT_VALID("V0001", "Validation error", HttpStatus.BAD_REQUEST),
	CONSTRAINT_VIOLATION("V0002", "Validation error", HttpStatus.BAD_REQUEST),
	DOMAIN_EXCEPTION("V0003", "Domain constraint violation", HttpStatus.BAD_REQUEST),
	DATA_INTEGRITY_VIOLATION("V0004", "Data integrity violation", HttpStatus.BAD_REQUEST),
	//FILESYSTEM
	INVALID_FILE("F0001", "Invalid file", HttpStatus.BAD_REQUEST),
	DIRECTORY_NOT_FOUND("F0001", "Not found directory", HttpStatus.NOT_FOUND),
	SIZE_LIMIT_EXCEEDED("F0002", "Files size are too big", HttpStatus.BAD_REQUEST),
	FILE_SIZE_LIMIT_EXCEEDED("F0003", "The file is too big", HttpStatus.BAD_REQUEST),
	FILE_STORAGE_EXCEPTION("F0004", "File IO error", HttpStatus.BAD_REQUEST),


	//MEMBER, FOLLOW\
	MEMBER_NOT_FOUND("M0001", "Not found member", HttpStatus.NOT_FOUND),
	FOLLOW_NOT_FOUND("M0002", "Not followed", HttpStatus.NOT_FOUND),

	//POST
	POST_NOT_FOUND("P0001", "Not found post", HttpStatus.NOT_FOUND),
	POST_ALREADY_LIKED("P0002", "Posts that you already like", HttpStatus.BAD_REQUEST),
	POST_ALREADY_UNLIKE("P0003", "Posts that you already unlike", HttpStatus.BAD_REQUEST),
	POST_IMAGE_NOT_FOUND("P0004", "Post image not found", HttpStatus.NOT_FOUND),

	//COMMENT
	COMMENT_NOT_FOUND("CM0001", "Comment not found", HttpStatus.NOT_FOUND),

	//AUTHENTICATION
	AUTHENTICATION_FAILED("A0001", "Authentication failed", HttpStatus.BAD_REQUEST),

	//TOKEN
	TOKEN_NOT_FOUND("T0001", "Not found token", HttpStatus.NOT_FOUND);

	private final String code;
	private final String message;
	private final HttpStatus httpStatus;

	ErrorCode(String code, String message, HttpStatus httpStatus) {
		this.code = code;
		this.message = message;
		this.httpStatus = httpStatus;
	}

	public String getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}

	public HttpStatus getStatus() {
		return httpStatus;
	}

	@Override
	public String toString() {
		return "ErrorCode[" +
			"type='" + name() + '\'' +
			",code='" + code + '\'' +
			", message='" + message + '\'' +
			']';
	}
}
