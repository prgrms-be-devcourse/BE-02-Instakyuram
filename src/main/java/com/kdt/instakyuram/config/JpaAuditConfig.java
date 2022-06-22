package com.kdt.instakyuram.config;

import java.util.Optional;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.kdt.instakyuram.security.jwt.JwtAuthentication;

@Configuration
@EnableJpaAuditing
public class JpaAuditConfig {

	@Bean
	public AuditorAware<String> autAuditorProvider() {
		return () -> {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

			if (authentication == null || !authentication.isAuthenticated() || isAnonymous(authentication)) {
				return Optional.empty();
			}

			JwtAuthentication user = (JwtAuthentication)authentication.getPrincipal();

			return Optional.ofNullable(user.id());
		};
	}

	/**
	 * note : anonymous 객체 필터링을 역할
	 * @param authentication
	 * @return
	 */
	private boolean isAnonymous(Authentication authentication) {
		return authentication instanceof AnonymousAuthenticationToken;
	}

}