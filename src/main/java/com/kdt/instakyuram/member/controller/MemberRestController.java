package com.kdt.instakyuram.member.controller;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseCookie;
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
import com.kdt.instakyuram.security.jwt.JwtConfigure;

@RestController
@RequestMapping("/api/members")
public class MemberRestController {

	private final MemberService memberService;
	private final JwtConfigure jwtConfigure;

	public MemberRestController(MemberService memberService, JwtConfigure jwtConfigure) {
		this.memberService = memberService;
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
			signinResponse.accessToken()).build();
		ResponseCookie refreshTokenCookie = ResponseCookie.from(jwtConfigure.refreshToken().header(),
			signinResponse.refreshToken()).build();
		response.setHeader("Set-Cookie", accessTokenCookie.toString());
		response.addHeader("Set-Cookie", refreshTokenCookie.toString());

		return new ApiResponse<>(signinResponse);
	}


}
