package com.kdt.instakyuram.security.jwt;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.kdt.instakyuram.exception.EntityNotFoundException;
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
	protected boolean shouldNotFilter(HttpServletRequest request) {
		return request.getRequestURI().endsWith("/api/members/signup") || request.getRequestURI()
			.endsWith("/api/members/signin") || SecurityContextHolder.getContext().getAuthentication() != null;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {
		try {
			authenticate(getAccessToken(request), request, response);
		} catch (JwtTokenNotFoundException e) {
			this.log.warn(e.getMessage());
		}
		filterChain.doFilter(request, response);
	}

	private String getAccessToken(HttpServletRequest request) {
		if (request.getCookies() != null) {
			return Arrays.stream(request.getCookies())
				.filter(cookie -> cookie.getName().equals(this.accessTokenHeader))
				.findFirst()
				.map(Cookie::getValue)
				.orElseThrow(() -> new JwtAccessTokenNotFoundException("AccessToken is not found"));
		} else {
			throw new JwtAccessTokenNotFoundException("AccessToken is not found.");
		}
	}

	private void authenticate(String accessToken, HttpServletRequest request, HttpServletResponse response) {
		try {
			Jwt.Claims claims = verify(accessToken);
			JwtAuthenticationToken authentication = createAuthenticationToken(claims, request, accessToken);
			SecurityContextHolder.getContext().setAuthentication(authentication);
			this.log.info("set Authentication");

		} catch (TokenExpiredException exception) {
			this.log.warn(exception.getMessage());
			refreshAuthentication(accessToken, request, response);
		} catch (JWTVerificationException exception) {
			log.warn(exception.getMessage());
		}
	}

	private JwtAuthenticationToken createAuthenticationToken(Jwt.Claims claims, HttpServletRequest request,
		String accessToken) {
		List<GrantedAuthority> authorities = this.jwt.getAuthorities(claims);
		if (claims.memberId != null && !authorities.isEmpty()) {
			JwtAuthentication authentication = new JwtAuthentication(accessToken, claims.memberId);
			JwtAuthenticationToken authenticationToken = new JwtAuthenticationToken(authentication, null, authorities);
			authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

			return authenticationToken;
		} else {
			throw new JWTDecodeException("Decode Error");
		}
	}

	private void refreshAuthentication(String accessToken, HttpServletRequest request, HttpServletResponse response) {
		try {
			String refreshToken = getRefreshToken(request);
			if (isValidRefreshToken(refreshToken, accessToken)) {
				String reIssuedAccessToken = accessTokenReIssue(accessToken);
				Jwt.Claims reIssuedClaims = verify(reIssuedAccessToken);
				JwtAuthenticationToken authentication = createAuthenticationToken(reIssuedClaims, request,
					reIssuedAccessToken);
				SecurityContextHolder.getContext().setAuthentication(authentication);
				response.addCookie(new Cookie(accessTokenHeader, reIssuedAccessToken));
			} else {
				log.warn("refreshToken expired");
			}
		} catch (JwtTokenNotFoundException | JWTVerificationException e) {
			this.log.warn(e.getMessage());
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

	private boolean isValidRefreshToken(String refreshToken, String accessToken) {
		try {
			TokenResponse foundRefreshToken = this.tokenService.findByToken(refreshToken);
			Long memberId = this.jwt.decode(accessToken).memberId;
			if (memberId.equals(foundRefreshToken.memberId())) {
				this.jwt.verify(foundRefreshToken.token());

				return true;
			}
		} catch (EntityNotFoundException | JWTVerificationException e) {
			log.warn(e.getMessage());

			return false;
		}
		return false;
	}

	private String accessTokenReIssue(String accessToken) {
		return jwt.generateAccessToken(this.jwt.decode(accessToken));
	}

	private Jwt.Claims verify(String token) {
		return jwt.verify(token);
	}
}