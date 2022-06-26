package com.kdt.instakyuram.exception;

import org.apache.commons.lang3.StringUtils;

import com.kdt.instakyuram.util.MessageUtils;

public class ServiceNotFoundException extends ServiceRuntimeException {

	public ServiceNotFoundException(ErrorCodeV2 errorCode, Object... values) {
		this(errorCode.getCode(), "error.comment.notfound.details", values);
	}

	public ServiceNotFoundException(String messageKey, String detailsKey, Object[] values) {
		super(messageKey, detailsKey, new String[]{values != null && values.length > 0 ? StringUtils.join(values, ", ") : ""});
	}

	@Override
	public String getMessage() {
		return MessageUtils.getMessage(getDetailsKey(), getParams());
	}

	@Override
	public String toString() {
		return MessageUtils.getMessage(getMessageKey());
	}
}
