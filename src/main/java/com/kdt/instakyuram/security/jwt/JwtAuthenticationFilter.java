package com.kdt.instakyuram.security.jwt;

import static org.apache.logging.log4j.util.Strings.isNotEmpty;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.GenericFilterBean;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.kdt.instakyuram.token.dto.TokenResponse;
import com.kdt.instakyuram.token.service.TokenService;

public class JwtAuthenticationFilter extends GenericFilterBean {
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
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws
		IOException,
		ServletException {
		HttpServletRequest request = (HttpServletRequest)req;
		HttpServletResponse response = (HttpServletResponse)res;
		if (isHaveToJwtCheck(request)) {
			try {
				String accessToken = getToken(request);
				if (accessToken != null) {
					try {
						Jwt.Claims claims = verify(accessToken);
						JwtAuthenticationToken authenticationToken = decodeClaims(claims, request, accessToken);
						SecurityContextHolder.getContext().setAuthentication(authenticationToken);
						this.log.info("set Authentication");
					} catch (TokenExpiredException e) {
						this.log.warn("AccessToken is expired");
						String refreshToken = getRefreshToken(request);
						try {
							if (isValidRefreshToken(refreshToken, accessToken)) {
								String reIssuedAccessToken = accessTokenReIssue(accessToken);
								Jwt.Claims reIssuedClaims = verify(reIssuedAccessToken);
								SecurityContextHolder.getContext()
									.setAuthentication(decodeClaims(reIssuedClaims, request, reIssuedAccessToken));
								response.addCookie(new Cookie(accessTokenHeader, reIssuedAccessToken));
							}
						} catch (JwtRefreshTokenNotFoundException exception) {
							this.log.warn("RefreshToken is not found");
						}
					} catch (JWTVerificationException e) {
						this.log.warn("AccessToken is not valid", e);
					}
				} else {
					this.log.info("There is no access token");
				}
			} catch (JwtAccessTokenNotFoundException e) {
				this.log.warn("accessToken not found");
			}

		}
		chain.doFilter(req, res);
	}

	private boolean isHaveToJwtCheck(HttpServletRequest request) {
		return !request.getRequestURI().endsWith("/api/members/signup")
			&& SecurityContextHolder.getContext().getAuthentication() == null;
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
		TokenResponse foundRefreshToken = this.tokenService.findByRefreshToken(refreshToken);
		String username = this.jwt.decode(accessToken).userId;
		if (username.equals(foundRefreshToken.userId())) {
			try {
				this.jwt.verify(foundRefreshToken.refreshToken());
			} catch (JWTVerificationException e) {
				this.log.warn("Invalid RefreshToken");
				return false;
			}
		}
		return true;
	}

	private List<GrantedAuthority> getAuthorities(Jwt.Claims claims) {
		String[] roles = claims.roles;

		return roles == null && roles.length == 0
			? Collections.emptyList()
			: Arrays.stream(roles)
			.map(SimpleGrantedAuthority::new)
			.collect(Collectors.toList());
	}

	private Jwt.Claims verify(String token) {
		return jwt.verify(token);
	}

	private String getToken(HttpServletRequest request) {
		if(request.getCookies() != null) {
			return Arrays.stream(request.getCookies())
				.filter(cookie -> cookie.getName().equals(this.accessTokenHeader))
				.findFirst()
				.map(Cookie::getValue)
				.orElseThrow(JwtAccessTokenNotFoundException::new);
		} else {
			throw new JwtAccessTokenNotFoundException();
		}
	}

	private String getRefreshToken(HttpServletRequest request) {
		if(request.getCookies() != null) {
			return Arrays.stream(request.getCookies())
				.filter(cookie -> cookie.getName().equals(this.refreshTokenHeader))
				.findFirst()
				.map(Cookie::getValue)
				.orElseThrow(JwtRefreshTokenNotFoundException::new);
		} else {
			throw new JwtRefreshTokenNotFoundException();
		}
	}
}
