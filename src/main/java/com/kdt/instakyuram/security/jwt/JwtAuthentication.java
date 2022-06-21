package com.kdt.instakyuram.security.jwt;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.logging.log4j.util.Strings.isNotEmpty;

public record JwtAuthentication(String token, String id) {
	public JwtAuthentication {
		checkArgument(isNotEmpty(token), "access token must be provided");
		checkArgument(isNotEmpty(id), "id must be provided");
	}

}
