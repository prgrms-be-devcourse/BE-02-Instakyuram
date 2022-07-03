package com.kdt.instakyuram.config;

import java.util.Arrays;

import org.springdoc.core.customizers.OpenApiCustomiser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
@Profile({"dev", "local", "mem"})
public class SwaggerConfig {
	@Bean
	public OpenApiCustomiser customOpenApi() {
		return openApi -> {
			String bearer = "bearer";
			openApi
				.info(new Info().title("instakyuram"))
				.components(
					openApi.getComponents().addSecuritySchemes(
						bearer,
						new SecurityScheme()
							.type(SecurityScheme.Type.HTTP)
							.scheme(bearer)
							.bearerFormat("JWT")
							.in(SecurityScheme.In.HEADER)
							.name("Authorization")
					)
				)
				.addSecurityItem(
					new SecurityRequirement()
						.addList(bearer, Arrays.asList("read", "write"))
				);
		};
	}
}