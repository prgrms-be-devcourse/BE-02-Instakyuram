package com.kdt.instakyuram;

import java.util.Optional;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

@TestConfiguration
@EnableJpaAuditing
public class JpaAuditTestConfig {

	@Bean
	public AuditorAware<String> autAuditorProvider() {
		return () -> {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

			if (authentication == null || !authentication.isAuthenticated()) {
				return Optional.empty();
			}

			User user = (User)authentication.getPrincipal();

			return Optional.ofNullable(user.getUsername());
		};
	}
}
