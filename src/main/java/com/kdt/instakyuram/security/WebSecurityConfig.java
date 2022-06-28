package com.kdt.instakyuram.security;

import javax.servlet.http.HttpServletResponse;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.kdt.instakyuram.security.jwt.Jwt;
import com.kdt.instakyuram.security.jwt.JwtAuthenticationFilter;
import com.kdt.instakyuram.auth.service.TokenService;

@EnableWebSecurity
@Configuration
@EnableConfigurationProperties({SecurityConfigProperties.class})
public class WebSecurityConfig {
	private final SecurityConfigProperties securityConfigProperties;

	public WebSecurityConfig(SecurityConfigProperties securityConfigProperties) {
		this.securityConfigProperties = securityConfigProperties;
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public Jwt jwt() {
		return new Jwt(
			this.securityConfigProperties.jwt().issuer(),
			this.securityConfigProperties.jwt().clientSecret(),
			this.securityConfigProperties.jwt().accessToken(),
			this.securityConfigProperties.jwt().refreshToken()
		);
	}

	public JwtAuthenticationFilter jwtAuthenticationFilter(Jwt jwt, TokenService tokenService) {
		return new JwtAuthenticationFilter(
			this.securityConfigProperties.jwt().accessToken().header(),
			this.securityConfigProperties.jwt().refreshToken().header(),
			jwt,
			tokenService
		);
	}

	@Bean
	public AccessDeniedHandler accessDeniedHandler() {
		return (request, response, e) -> {
			response.sendRedirect("/members/signin");
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		};
	}

	@Bean
	public AuthenticationEntryPoint authenticationEntryPoint() {
		return (request, response, e) -> {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.sendRedirect("/members/signin");
		};
	}

	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
		return web -> web.ignoring().antMatchers(this.securityConfigProperties.patterns().ignoring());
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http, Jwt jwt, TokenService tokenService) throws
		Exception {
		http
			.authorizeRequests()
			.antMatchers(this.securityConfigProperties.patterns().permitAll()).permitAll()
			.anyRequest().authenticated()
			.and()
			.formLogin().disable()
			.csrf().disable()
			.headers().disable()
			.httpBasic().disable()
			.rememberMe().disable()
			.logout().disable()
			.sessionManagement()
			.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			.and()
			.exceptionHandling()
			.accessDeniedHandler(accessDeniedHandler())
			.authenticationEntryPoint(authenticationEntryPoint())
			.and()
			.addFilterBefore(jwtAuthenticationFilter(jwt, tokenService), UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}
}

