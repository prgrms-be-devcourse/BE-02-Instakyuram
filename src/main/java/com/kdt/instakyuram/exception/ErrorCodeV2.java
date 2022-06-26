package com.kdt.instakyuram.exception;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public enum ErrorCodeV2 {
	COMMENT_NOT_FOUND("error.comment.notfound", "error.comment.notfound.details");

	private final String code;
	private final String details;

	ErrorCodeV2(String code, String details) {
		this.code = code;
		this.details = details;
	}

	public String getCode() {
		return code;
	}

	public String getDetails() {
		return details;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
			.append("code", code)
			.append("details", details)
			.toString();
	}
}
