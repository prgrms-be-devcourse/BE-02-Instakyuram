package com.kdt.instakyuram.security.jwt;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

import lombok.Builder;

public class Jwt {
	private final String issuer;
	private final JwtConfigure.AccessTokenProperties accessTokenProperties;
	private final JwtConfigure.RefreshTokenProperties refreshTokenProperties;
	private final Algorithm algorithm;
	private final JWTVerifier jwtVerifier;

	public Jwt(String issuer, String clientSecret, JwtConfigure.AccessTokenProperties accessTokenProperties,
		JwtConfigure.RefreshTokenProperties refreshTokenProperties) {
		this.issuer = issuer;
		this.algorithm = Algorithm.HMAC512(clientSecret);
		this.accessTokenProperties = accessTokenProperties;
		this.refreshTokenProperties = refreshTokenProperties;
		this.jwtVerifier = JWT.require(algorithm)
			.withIssuer(this.issuer)
			.build();
	}

	public String generateAccessToken(Claims claims) {
		Date now = new Date();
		JWTCreator.Builder builder = JWT.create();

		builder.withSubject(claims.memberId.toString());
		builder.withIssuer(this.issuer);
		builder.withIssuedAt(now);

		if (accessTokenProperties.expirySeconds() > 0) {
			builder.withExpiresAt(new Date(now.getTime() + accessTokenProperties.expirySeconds() * 1000L));
		}
		builder.withClaim("memberId", claims.memberId);
		builder.withArrayClaim("roles", claims.roles);

		return builder.sign(algorithm);
	}

	public String generateAccessToken(Long id, String[] roles) {
		Date now = new Date();
		JWTCreator.Builder builder = JWT.create();

		builder.withSubject(id.toString());
		builder.withIssuer(this.issuer);
		builder.withIssuedAt(now);

		if (accessTokenProperties.expirySeconds() > 0) {
			builder.withExpiresAt(new Date(now.getTime() + accessTokenProperties.expirySeconds() * 1000L));
		}
		builder.withClaim("memberId", id);
		builder.withArrayClaim("roles", roles);
		return builder.sign(algorithm);
	}

	public String generateRefreshToken() {
		Date now = new Date();
		JWTCreator.Builder builder = JWT.create();
		builder.withIssuer(this.issuer);
		builder.withIssuedAt(now);
		if (refreshTokenProperties.expirySeconds() > 0) {
			builder.withExpiresAt(new Date(now.getTime() + refreshTokenProperties.expirySeconds() * 1000L));
		}

		return builder.sign(this.algorithm);
	}

	public Claims decode(String token) {
		return new Claims(JWT.decode(token));

	}

	public Claims verify(String token) {
		return new Claims(this.jwtVerifier.verify(token));

	}

	public List<GrantedAuthority> getAuthorities(Jwt.Claims claims) {
		String[] roles = claims.roles;

		return roles == null || roles.length == 0
			? Collections.emptyList()
			: Arrays.stream(roles)
			.map(SimpleGrantedAuthority::new)
			.collect(Collectors.toList());
	}

	public static class Claims {
		Long memberId;
		String[] roles;
		Date iat;
		Date exp;

		private Claims() {
		}

		Claims(DecodedJWT decodedJWT) {
			Claim memberId = decodedJWT.getClaim("memberId");
			if (!memberId.isNull()) {
				this.memberId = memberId.asLong();
			}
			Claim roles = decodedJWT.getClaim("roles");
			if (!roles.isNull()) {
				this.roles = roles.asArray(String.class);
			}
			this.iat = decodedJWT.getIssuedAt();
			this.exp = decodedJWT.getExpiresAt();
		}

		@Builder
		Claims(Long memberId, String[] roles, Date iat, Date exp) {
			this.memberId = memberId;
			this.roles = roles;
			this.iat = iat;
			this.exp = exp;
		}
	}
}
