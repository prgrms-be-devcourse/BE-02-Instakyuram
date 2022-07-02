package com.kdt.instakyuram.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableConfigurationProperties({CorsConfigProperties.class})
public class WebMvcConfig implements WebMvcConfigurer {

	private final CorsConfigProperties corsConfigProperties;

	public WebMvcConfig(CorsConfigProperties corsConfigProperties) {
		this.corsConfigProperties = corsConfigProperties;
	}

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping(corsConfigProperties.api())
			.allowedOrigins(corsConfigProperties.url())
			.allowedMethods(corsConfigProperties.method())
		;
	}
}
