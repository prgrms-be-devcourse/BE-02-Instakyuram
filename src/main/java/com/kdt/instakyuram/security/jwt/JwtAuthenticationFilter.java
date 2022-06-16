package com.kdt.instakyuram.security.jwt;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.springframework.web.filter.GenericFilterBean;

public class JwtAuthenticationFilter extends GenericFilterBean {
	private final String jwtHeader;
	private final Jwt jwt;

	public JwtAuthenticationFilter(String jwtHeader, Jwt jwt) {
		this.jwtHeader = jwtHeader;
		this.jwt = jwt;
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws
		IOException,
		ServletException {

	}
}
