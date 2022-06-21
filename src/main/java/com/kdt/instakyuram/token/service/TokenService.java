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

	public TokenResponse findByToken(String token) {
		Token foundToken = tokenRepository.findById(token).orElseThrow(JwtTokenNotFoundException::new);

		return new TokenResponse(foundToken.getRefreshToken(), foundToken.getUserId().toString());
	}

	public String save(String refreshToken, Long userId) {
		return tokenRepository.save(new Token(refreshToken, userId)).getRefreshToken();
	}
}
