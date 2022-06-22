
package com.kdt.instakyuram.security;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.*;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
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
import com.kdt.instakyuram.security.jwt.JwtConfigure;
import com.kdt.instakyuram.token.service.TokenService;

@EnableWebSecurity
@Configuration
@ConfigurationPropertiesScan("com.kdt.instakyuram.security")
public class WebSecurityConfigure {
	private final Logger log = LoggerFactory.getLogger(getClass());

	private final JwtConfigure jwtConfigure;

	public WebSecurityConfigure(JwtConfigure jwtConfigure) {
		this.jwtConfigure = jwtConfigure;
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public Jwt jwt() {
		return new Jwt(
			this.jwtConfigure.issuer(),
			this.jwtConfigure.clientSecret(),
			this.jwtConfigure.accessToken(),
			this.jwtConfigure.refreshToken()
		);
	}

	public JwtAuthenticationFilter jwtAuthenticationFilter(Jwt jwt, TokenService tokenService) {
		return new JwtAuthenticationFilter(
			this.jwtConfigure.accessToken().header(),
			this.jwtConfigure.refreshToken().header(),
			jwt,
			tokenService
		);
	}

	@Bean
	public AccessDeniedHandler accessDeniedHandler() {
		log.warn("accessDeniedHandler");
		return (request, response, e) -> {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			response.setContentType("text/plain;charset=UTF-8");
			response.getWriter().write("ACCESS DENIED");
			response.getWriter().flush();
			response.getWriter().close();
		};
	}

	@Bean
	public AuthenticationEntryPoint authenticationEntryPoint() {
		return (request, response, e) -> {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.setContentType("text/plain;charset=UTF-8");
			response.getWriter().write("UNAUTHORIZED");
			response.getWriter().flush();
			response.getWriter().close();
		};
	}

	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
		return web -> web.ignoring().antMatchers("/css/**", "/js/**", "/webjars/**", "/images/**");
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http, Jwt jwt, TokenService tokenService) throws
		Exception {
		http
			.authorizeRequests()
			.antMatchers("/api/members/signup", "/api/members/signin").permitAll()
			.anyRequest().authenticated()
			.and()
			.formLogin()
			.disable()
			.csrf()
			.disable()
			.headers()
			.disable()
			.httpBasic()
			.disable()
			.rememberMe()
			.disable()
			.logout()
			.disable()
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

