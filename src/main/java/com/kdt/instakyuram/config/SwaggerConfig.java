package com.kdt.instakyuram.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@Configuration
@EnableConfigurationProperties({SwaggerConfigProperties.class})
public class SwaggerConfig {

	private final SwaggerConfigProperties swaggerConfigProperties;

	public SwaggerConfig(SwaggerConfigProperties swaggerConfigProperties) {
		this.swaggerConfigProperties = swaggerConfigProperties;
	}

	private ApiInfo swaggerInfo() {
		return new ApiInfoBuilder().title(swaggerConfigProperties.title())
			.version(swaggerConfigProperties.version())
			.description(swaggerConfigProperties.description())
			.build();
	}

	@Bean
	public Docket swaggerApi() {
		return new Docket(DocumentationType.SWAGGER_2)
			.apiInfo(swaggerInfo()).select()
			.apis(RequestHandlerSelectors.basePackage(swaggerConfigProperties.basePackage()))
			.paths(PathSelectors.ant(swaggerConfigProperties.apiPath()))
			.build()
			.useDefaultResponseMessages(false);
	}
}

