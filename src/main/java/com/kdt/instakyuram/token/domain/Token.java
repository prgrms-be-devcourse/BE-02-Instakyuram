package com.kdt.instakyuram.token.domain;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.kdt.instakyuram.common.BaseEntity;

@Entity
public class Token extends BaseEntity {

	@Id
	private String refreshToken;

	private Long userId;

	protected Token() {
	}

	public Token(String refreshToken, Long userId) {
		this.refreshToken = refreshToken;
		this.userId = userId;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public Long getUserId() {
		return userId;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
			.append("refreshToken", refreshToken)
			.append("userId", userId)
			.append("createdAt", super.getCreatedAt())
			.append("updatedAt", super.getUpdatedAt())
			.append("createdBy", super.getCreatedBy())
			.append("updatedBy", super.getUpdatedBy())
			.toString();
	}
}
