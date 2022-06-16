package com.kdt.instakyuram.security.jwt;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.logging.log4j.util.Strings.isNotEmpty;

public class JwtAuthentication {
	private final String token;
	private final String username;

	public JwtAuthentication(String token, String username) {
		checkArgument(isNotEmpty(token), "token must be provided");
		checkArgument(isNotEmpty(token), "username must be provided");
		this.token = token;
		this.username = username;
	}

}
