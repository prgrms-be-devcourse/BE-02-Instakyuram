package com.kdt.instakyuram.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.kdt.instakyuram.config.log.LogInterceptor;

@Configuration
@EnableConfigurationProperties({CorsConfigProperties.class})
public class WebMvcConfig implements WebMvcConfigurer {

	private final CorsConfigProperties corsConfigProperties;
	private final LogInterceptor logInterceptor;

	public WebMvcConfig(CorsConfigProperties corsConfigProperties, LogInterceptor logInterceptor) {
		this.corsConfigProperties = corsConfigProperties;
		this.logInterceptor = logInterceptor;
	}

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping(corsConfigProperties.api())
			.allowedOrigins(corsConfigProperties.url())
			.allowedMethods(corsConfigProperties.method())
		;
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(logInterceptor);
	}
}
