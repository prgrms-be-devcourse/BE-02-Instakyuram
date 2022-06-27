package com.kdt.instakyuram.common;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class ApiResponse<T> {
	private T response;

	private ApiResponse() {

	}

	public ApiResponse(T response) {
		this.response = response;
	}

	public T getResponse() {
		return response;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
			.append("response", response)
			.toString();
	}
}
