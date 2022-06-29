package com.kdt.instakyuram.config;

import static java.util.Collections.singletonList;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.SecurityScheme;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@Configuration
public class SwaggerConfig {

	private ApiInfo swaggerInfo() {
		return new ApiInfoBuilder().title("KkyuTeam-Instagram")
			.version("v1.0.0")
			.description("api docs swagger ")
			.build();
	}

	@Bean
	public Docket swaggerApi() {
		return new Docket(DocumentationType.SWAGGER_2)
			.apiInfo(swaggerInfo()).select()
			.apis(RequestHandlerSelectors.basePackage("com.kdt.instakyuram"))
			.paths(PathSelectors.any())
			.build()
			.securitySchemes(singletonList(apiKeyAccessToken()))
			.useDefaultResponseMessages(false);

	}

	// note : swagger - ui를 에러 발생.
	private List<SecurityScheme> getTokensApiKey() {
		return List.of(apiKeyAccessToken(), apiKeyRefreshToken());
	}

	/**
	 * todo : 인증 쿠키 셋팅하기 [현재 2개 accessToken, refreshToken 설정하려는데 1개만 들어가고 있음.] from: whyWhale
	 * @return
	 */
	private ApiKey apiKeyAccessToken() {
		return (new ApiKey("Authorization", "aToken", "cookie"));
	}

	private ApiKey apiKeyRefreshToken() {
		return (new ApiKey("Authorization", "rToken", "cookie"));
	}
}
