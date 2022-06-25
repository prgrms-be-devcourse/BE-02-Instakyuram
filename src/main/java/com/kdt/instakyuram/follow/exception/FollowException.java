package com.kdt.instakyuram.follow.exception;

import com.kdt.instakyuram.exception.DomainException;
import com.kdt.instakyuram.exception.ErrorCode;

public class FollowException extends DomainException {

	public FollowException(ErrorCode errorCode, String message) {
		super(errorCode, message);
	}

}
