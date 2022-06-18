package com.kdt.instakyuram.token.domain;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Token {

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
}
