package com.kdt.instakyuram.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConstructorBinding
@ConfigurationProperties(prefix = "cors.allowed")
public record CorsConfigure(
	String api,
	String[] url,
	String[] method
) { }
