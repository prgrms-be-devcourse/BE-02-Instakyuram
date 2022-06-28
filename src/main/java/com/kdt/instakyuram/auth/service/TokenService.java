package com.kdt.instakyuram.auth.service;

import java.text.MessageFormat;

import org.springframework.stereotype.Service;

import com.kdt.instakyuram.auth.repository.TokenRepository;
import com.kdt.instakyuram.exception.EntityNotFoundException;
import com.kdt.instakyuram.exception.ErrorCode;
import com.kdt.instakyuram.auth.domain.Token;
import com.kdt.instakyuram.auth.dto.TokenResponse;

@Service
public class TokenService {
	private final TokenRepository tokenRepository;

	public TokenService(TokenRepository tokenRepository) {
		this.tokenRepository = tokenRepository;
	}

	public TokenResponse findByToken(String token) {
		Token foundToken = tokenRepository.findById(token).orElseThrow(() -> new EntityNotFoundException(
			ErrorCode.TOKEN_NOT_FOUND, MessageFormat.format("Token = {0}", token)));

		return new TokenResponse(foundToken.token(), foundToken.getMemberId());
	}

	public String save(String refreshToken, Long userId) {
		return tokenRepository.save(new Token(refreshToken, userId)).token();
	}

	public void deleteByToken(String token) {
		tokenRepository.delete(
			tokenRepository.findById(token)
				.orElseThrow(() -> new EntityNotFoundException(ErrorCode.TOKEN_NOT_FOUND,
					MessageFormat.format("Token = {0}", token)))
		);
	}
}
