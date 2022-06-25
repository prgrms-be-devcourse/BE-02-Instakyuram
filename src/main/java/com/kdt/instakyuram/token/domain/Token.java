package com.kdt.instakyuram.token.domain;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.kdt.instakyuram.common.BaseEntity;

@Entity
public class Token extends BaseEntity {

	@Id
	private String token;

	private Long memberId;

	protected Token() {
	}

	public Token(String token, Long memberId) {
		this.token = token;
		this.memberId = memberId;
	}

	public String token() {
		return token;
	}

	public Long getMemberId() {
		return memberId;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
			.append("token", token)
			.append("memberId", memberId)
			.append("createdAt", super.getCreatedAt())
			.append("updatedAt", super.getUpdatedAt())
			.append("createdBy", super.getCreatedBy())
			.append("updatedBy", super.getUpdatedBy())
			.toString();
	}
}
