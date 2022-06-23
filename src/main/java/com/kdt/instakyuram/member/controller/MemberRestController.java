package com.kdt.instakyuram.member.controller;

import java.util.Arrays;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseCookie;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kdt.instakyuram.common.ApiResponse;
import com.kdt.instakyuram.member.dto.MemberRequest;
import com.kdt.instakyuram.member.dto.MemberResponse;
import com.kdt.instakyuram.member.service.MemberService;
import com.kdt.instakyuram.security.jwt.JwtAuthentication;
import com.kdt.instakyuram.security.jwt.JwtConfigure;
import com.kdt.instakyuram.token.service.TokenService;

@RestController
@RequestMapping("/api/members")
public class MemberRestController {

	private final MemberService memberService;
	private final TokenService tokenService;
	private final JwtConfigure jwtConfigure;

	public MemberRestController(MemberService memberService, TokenService tokenService, JwtConfigure jwtConfigure) {
		this.memberService = memberService;
		this.tokenService = tokenService;
		this.jwtConfigure = jwtConfigure;
	}

	@GetMapping("/{userId}")
	ApiResponse<MemberResponse> getById(@PathVariable Long userId) {
		return new ApiResponse<>(memberService.findById(userId));
	}

	@PostMapping("/signup")
	ApiResponse<MemberResponse.SignupResponse> signup(@RequestBody MemberRequest.SignupRequest request) {
		return new ApiResponse<>(memberService.signup(request));
	}

	@PostMapping("/signin")
	public ApiResponse<MemberResponse.SigninResponse> signIn(@RequestBody MemberRequest.SignupRequest request,
		HttpServletResponse response) {
		MemberResponse.SigninResponse signinResponse = this.memberService.signin(request.username(),
			request.password());
		ResponseCookie accessTokenCookie = ResponseCookie.from(jwtConfigure.accessToken().header(),
			signinResponse.accessToken()).path("/").build();
		ResponseCookie refreshTokenCookie = ResponseCookie.from(jwtConfigure.refreshToken().header(),
			signinResponse.refreshToken()).path("/").build();
		response.setHeader("Set-Cookie", accessTokenCookie.toString());
		response.addHeader("Set-Cookie", refreshTokenCookie.toString());

		return new ApiResponse<>(signinResponse);
	}

	@PostMapping("/signout")
	public ApiResponse<String> signout(@AuthenticationPrincipal JwtAuthentication principal, HttpServletRequest request,
		HttpServletResponse response) {
		tokenService.save(principal.token(), Long.parseLong(principal.id()));
		Arrays.stream(request.getCookies())
			.filter(cookie -> cookie.getName().equals(jwtConfigure.refreshToken().header()))
			.findFirst()
			.ifPresent(cookie -> tokenService.deleteByToken(cookie.getValue()));
		Cookie accessTokenCookie = new Cookie(jwtConfigure.accessToken().header(), null);
		Cookie refreshTokenCookie = new Cookie(jwtConfigure.refreshToken().header(), null);
		accessTokenCookie.setMaxAge(0);
		refreshTokenCookie.setMaxAge(0);
		response.addCookie(accessTokenCookie);
		response.addCookie(refreshTokenCookie);

		return new ApiResponse<>("signed out");
	}
}
