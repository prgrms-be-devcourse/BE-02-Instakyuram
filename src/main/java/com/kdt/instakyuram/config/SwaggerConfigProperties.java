package com.kdt.instakyuram.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConstructorBinding
@ConfigurationProperties(prefix = "swagger")
public record SwaggerConfigProperties(
	String title,
	String version,
	String description,
	String apiPath,
	String basePackage
) {
}
