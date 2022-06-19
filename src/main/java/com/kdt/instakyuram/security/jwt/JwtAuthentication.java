package com.kdt.instakyuram.security.jwt;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.logging.log4j.util.Strings.isNotEmpty;

public record JwtAuthentication(String token, String username) {
	public JwtAuthentication {
		checkArgument(isNotEmpty(token), "token must be provided");
		checkArgument(isNotEmpty(token), "username must be provided");
	}

}
