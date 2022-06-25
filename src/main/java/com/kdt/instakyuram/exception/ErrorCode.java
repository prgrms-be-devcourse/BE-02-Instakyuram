package com.kdt.instakyuram.exception;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public enum ErrorCode {
	DOMAIN_EXCEPTION("999", "도메인 제약 조건 위반");

	private final String code;
	private final String description;

	ErrorCode(String code, String description) {
		this.code = code;
		this.description = description;
	}

	public String getCode() {
		return code;
	}

	public String getDescription() {
		return description;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
			.append("code", code)
			.append("description", description)
			.toString();
	}
}
