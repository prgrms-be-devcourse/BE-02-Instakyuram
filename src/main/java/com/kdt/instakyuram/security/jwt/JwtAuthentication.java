package com.kdt.instakyuram.security.jwt;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.logging.log4j.util.Strings.isNotBlank;
import static org.apache.logging.log4j.util.Strings.isNotEmpty;

public record JwtAuthentication(String token, Long id, String username) {
	public JwtAuthentication {
		checkArgument(isNotEmpty(token), "access token must be provided");
		checkArgument(id != null, "id must be provided");
		checkArgument(isNotBlank(username), "username must be provided");
	}

}
