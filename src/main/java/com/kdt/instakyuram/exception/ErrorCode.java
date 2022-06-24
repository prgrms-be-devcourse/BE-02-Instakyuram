package com.kdt.instakyuram.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
	//COMMON
	INTERNAL_SEVER_ERROR("C001", "Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR),
	NOT_FOUND_EXCEPTION("C0002", "Not found exception", HttpStatus.BAD_REQUEST),

	//VALIDATION
	METHOD_ARGUMENT_NOT_VALID("V0001", "Validation Error", HttpStatus.BAD_REQUEST),
	CONSTRAINT_VIOLATION("V0002", "Validation Error", HttpStatus.BAD_REQUEST),

	//FILESYSTEM
	INVALID_FILE("F0001", "Invalid File", HttpStatus.BAD_REQUEST),
	DIRECTORY_NOT_FOUND("F0001", "Directory not found", HttpStatus.BAD_REQUEST),

	//MEMBER
	MEMBER_NOT_FOUND("M0001", "Member Not Found", HttpStatus.BAD_REQUEST),

	//POST
	POST_NOT_FOUND("P0001", "Post Not Found", HttpStatus.BAD_REQUEST),
	POST_ALREADY_LIKED("P0002", "Posts that you already like", HttpStatus.BAD_REQUEST),
	POST_ALREADY_UNLIKED("P0003", "Posts that you already unlike", HttpStatus.BAD_REQUEST),
	POST_IMAGE_NOT_FOUND("P0004", "Post Image Not Found", HttpStatus.BAD_REQUEST),

	//COMMENT
	COMMENT_NOT_FOUND("CM0001", "Post Not Found", HttpStatus.BAD_REQUEST),

	//AUTHENTICATION
	AUTHENTICATION_FAILED("A0001", "Authentication failed", HttpStatus.BAD_REQUEST);

	private final String code;
	private final String message;
	private final HttpStatus httpStatus;

	ErrorCode(String code, String message, HttpStatus httpStatus) {
		this.code = code;
		this.message = message;
		this.httpStatus = httpStatus;
	}

	public String code() {
		return code;
	}

	public String message() {
		return message;
	}

	public HttpStatus httpStatus() {
		return httpStatus;
	}
}
