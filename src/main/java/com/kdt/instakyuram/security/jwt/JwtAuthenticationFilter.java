package com.kdt.instakyuram.security.jwt;

import static org.apache.logging.log4j.util.Strings.isNotEmpty;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.kdt.instakyuram.token.dto.TokenResponse;
import com.kdt.instakyuram.token.service.TokenService;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
	private final String accessTokenHeader;
	private final String refreshTokenHeader;
	private final Jwt jwt;
	private final Logger log = LoggerFactory.getLogger(getClass());
	private final TokenService tokenService;

	public JwtAuthenticationFilter(String jwtHeader, String jwtRefreshHeader, Jwt jwt,
		TokenService tokenService) {
		this.accessTokenHeader = jwtHeader;
		this.refreshTokenHeader = jwtRefreshHeader;
		this.jwt = jwt;
		this.tokenService = tokenService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {
		try {
			String accessToken = getAccessToken(request);
			try {
				Jwt.Claims claims = verify(accessToken);
				JwtAuthenticationToken authenticationToken = decodeClaims(claims, request, accessToken);
				SecurityContextHolder.getContext().setAuthentication(authenticationToken);
				this.log.info("set Authentication");
			} catch (TokenExpiredException e) {
				this.log.warn(e.getMessage());
				String refreshToken = getRefreshToken(request);
				try {
					if (isValidRefreshToken(refreshToken, accessToken)) {
						String reIssuedAccessToken = accessTokenReIssue(accessToken);
						Jwt.Claims reIssuedClaims = verify(reIssuedAccessToken);
						SecurityContextHolder.getContext()
							.setAuthentication(decodeClaims(reIssuedClaims, request, reIssuedAccessToken));
						response.addCookie(new Cookie(accessTokenHeader, reIssuedAccessToken));
					}
				} catch (JwtRefreshTokenNotFoundException notFoundException) {
					this.log.warn(notFoundException.getMessage());
				}
			} catch (JWTVerificationException e) {
				this.log.warn(e.getMessage());
			}
		} catch (JwtAccessTokenNotFoundException | TokenExpiredException e) {
			this.log.warn(e.getMessage());
		}
		filterChain.doFilter(request, response);
	}

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
		return request.getRequestURI().endsWith("/api/members/signup") || request.getRequestURI()
			.endsWith("/api/members/signin") || SecurityContextHolder.getContext().getAuthentication() != null;
	}

	private String accessTokenReIssue(String accessToken) {
		return jwt.generateAccessToken(this.jwt.decode(accessToken));
	}

	private JwtAuthenticationToken decodeClaims(Jwt.Claims claims, HttpServletRequest request, String accessToken) {
		String username = claims.userId;
		List<GrantedAuthority> authorities = getAuthorities(claims);
		if (isNotEmpty(username) && !authorities.isEmpty()) {
			JwtAuthentication authentication = new JwtAuthentication(accessToken, username);
			JwtAuthenticationToken authenticationToken = new JwtAuthenticationToken(authentication, null, authorities);
			authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

			return authenticationToken;
		} else {
			throw new JWTDecodeException("Decode Error");
		}
	}

	private boolean isValidRefreshToken(String refreshToken, String accessToken) {
		TokenResponse foundRefreshToken = this.tokenService.findByToken(refreshToken);
		String username = this.jwt.decode(accessToken).userId;
		if (username.equals(foundRefreshToken.userId())) {
			try {
				this.jwt.verify(foundRefreshToken.refreshToken());
			} catch (JWTVerificationException e) {
				this.log.warn(e.getMessage());
				return false;
			}
		}
		return true;
	}

	private List<GrantedAuthority> getAuthorities(Jwt.Claims claims) {
		String[] roles = claims.roles;

		return roles == null || roles.length == 0
			? Collections.emptyList()
			: Arrays.stream(roles)
			.map(SimpleGrantedAuthority::new)
			.collect(Collectors.toList());
	}

	private Jwt.Claims verify(String token) {
		return jwt.verify(token);
	}

	private String getAccessToken(HttpServletRequest request) {
		if (request.getCookies() != null) {
			String accessToken = Arrays.stream(request.getCookies())
				.filter(cookie -> cookie.getName().equals(this.accessTokenHeader))
				.findFirst()
				.map(Cookie::getValue)
				.orElseThrow(JwtAccessTokenNotFoundException::new);
			try {
				tokenService.findByToken(accessToken);
				throw new TokenExpiredException("already signed out token");
			} catch (JwtTokenNotFoundException e) {
				return accessToken;
			}
		} else {
			throw new JwtAccessTokenNotFoundException("AccessToken is not found.");
		}
	}

	private String getRefreshToken(HttpServletRequest request) {
		if (request.getCookies() != null) {
			return Arrays.stream(request.getCookies())
				.filter(cookie -> cookie.getName().equals(this.refreshTokenHeader))
				.findFirst()
				.map(Cookie::getValue)
				.orElseThrow(() -> new JwtRefreshTokenNotFoundException("RefreshToken is not found."));
		} else {
			throw new JwtRefreshTokenNotFoundException();
		}
	}
}