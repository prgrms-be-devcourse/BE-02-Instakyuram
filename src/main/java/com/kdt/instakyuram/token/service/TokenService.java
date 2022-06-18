package com.kdt.instakyuram.token.service;

import org.springframework.stereotype.Service;

import com.kdt.instakyuram.security.jwt.JwtRefreshTokenNotFoundException;
import com.kdt.instakyuram.token.repository.TokenRepository;
import com.kdt.instakyuram.token.domain.Token;
import com.kdt.instakyuram.token.dto.TokenResponse;

@Service
public class TokenService {
	private final TokenRepository tokenRepository;

	public TokenService(TokenRepository tokenRepository) {
		this.tokenRepository = tokenRepository;
	}

	public TokenResponse findByRefreshToken(String refreshToken) {
		Token token = tokenRepository.findById(refreshToken).orElseThrow(JwtRefreshTokenNotFoundException::new);
		return new TokenResponse(token.getRefreshToken(), token.getUserId().toString());
	}

	public String save(String refreshToken, Long userId) {
		return tokenRepository.save(new Token(refreshToken, userId)).getRefreshToken();
	}
}
