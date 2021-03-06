package com.kdt.instakyuram.configure;

import java.util.Optional;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.kdt.instakyuram.security.jwt.JwtAuthentication;

@TestConfiguration
@EnableJpaAuditing
public class TestJpaAuditConfig {

	@Bean
	public AuditorAware<String> autAuditorProvider() {
		return () -> {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

			if (authentication == null || !authentication.isAuthenticated()) {
				return Optional.empty();
			}

			JwtAuthentication user = (JwtAuthentication)authentication.getPrincipal();

			return Optional.ofNullable(user.id().toString());
		};
	}
}
