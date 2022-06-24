package com.kdt.instakyuram.config;

import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@ConfigurationPropertiesScan("com.kdt.instakyuram.config")
public class WebMvcConfig implements WebMvcConfigurer {

	private final CorsConfigure corsConfigure;

	public WebMvcConfig(CorsConfigure corsConfigure) {
		this.corsConfigure = corsConfigure;
	}

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping(corsConfigure.api())
			.allowedOrigins(corsConfigure.url())
			.allowedMethods(corsConfigure.method())
		;
	}
}
