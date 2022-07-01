package com.kdt.instakyuram.user.member.controller;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.http.ResponseCookie;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kdt.instakyuram.auth.service.TokenService;
import com.kdt.instakyuram.common.ApiResponse;
import com.kdt.instakyuram.exception.NotAuthenticationException;
import com.kdt.instakyuram.security.jwt.Jwt;
import com.kdt.instakyuram.security.jwt.JwtAuthentication;
import com.kdt.instakyuram.security.jwt.JwtAuthenticationToken;
import com.kdt.instakyuram.user.member.dto.MemberRequest;
import com.kdt.instakyuram.user.member.dto.MemberResponse;
import com.kdt.instakyuram.user.member.service.MemberService;

import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;

@Api(tags = "회원 api")
@RestController
@RequestMapping("/api/members")
public class MemberRestController {

	private final MemberService memberService;
	private final TokenService tokenService;
	private final Jwt jwt;

	public MemberRestController(MemberService memberService, TokenService tokenService, Jwt jwt) {
		this.memberService = memberService;
		this.tokenService = tokenService;
		this.jwt = jwt;
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
	public ApiResponse<MemberResponse.SigninResponse> signIn(@RequestBody MemberRequest.SignupRequest signupRequest,
		HttpServletRequest request, HttpServletResponse response) {
		MemberResponse.SigninResponse signinResponse = this.memberService.signin(signupRequest.username(),
			signupRequest.password());
		ResponseCookie accessTokenCookie = ResponseCookie.from(jwt.accessTokenProperties().header(),
			signinResponse.accessToken()).path("/").build();
		ResponseCookie refreshTokenCookie = ResponseCookie.from(jwt.refreshTokenProperties().header(),
			signinResponse.refreshToken()).path("/").build();

		Jwt.Claims claims = jwt.verify(signinResponse.accessToken());
		JwtAuthentication authentication = new JwtAuthentication(signinResponse.accessToken(),
			signinResponse.id(), signinResponse.username());
		JwtAuthenticationToken authenticationToken = new JwtAuthenticationToken(authentication, null,
			jwt.getAuthorities(claims));
		authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
		SecurityContextHolder.getContext().setAuthentication(authenticationToken);
		response.setHeader("Set-Cookie", accessTokenCookie.toString());
		response.addHeader("Set-Cookie", refreshTokenCookie.toString());

		return new ApiResponse<>(signinResponse);
	}

	@PostMapping("/signout")
	public ApiResponse<String> signout(@AuthenticationPrincipal JwtAuthentication auth, HttpServletRequest request,
		HttpServletResponse response) {
		if (auth == null) {
			throw new NotAuthenticationException("로그인이 필요합니다.");
		}
		Arrays.stream(request.getCookies())
			.filter(cookie -> cookie.getName().equals(jwt.refreshTokenProperties().header()))
			.findFirst()
			.ifPresent(cookie -> tokenService.deleteByToken(cookie.getValue()));
		Cookie accessTokenCookie = new Cookie(this.jwt.accessTokenProperties().header(), null);
		Cookie refreshTokenCookie = new Cookie(this.jwt.refreshTokenProperties().header(), null);
		accessTokenCookie.setMaxAge(0);
		accessTokenCookie.setPath("/");
		refreshTokenCookie.setMaxAge(0);
		refreshTokenCookie.setPath("/");
		response.addCookie(accessTokenCookie);
		response.addCookie(refreshTokenCookie);

		return new ApiResponse<>("signed out");
	}

	@Operation(
		summary = "해당 사용자의 팔로우 목록 조회",
		description = "자기 자신 또는 타인의 팔로우 목록을 조회합니다.",
		responses = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "팔로우한 사용자 목록 조회 성공"),
		}
	)
	@GetMapping("/{username}/followers")
	public ApiResponse<List<MemberResponse.FollowerResponse>> getAdditionalFollowers(
		@Parameter(
			name = "사용자 username", description = "해당 사용자의 username 을 입력합니다.", in = ParameterIn.PATH, required = true
		) @PathVariable String username,
		@Parameter(
			name = "사용자 id[cursor]", description = "사용자에게 보여준 목록 중 마지막 memberId[커서]", in = ParameterIn.PATH, required = true
		) @Valid @RequestParam @NotNull Long lastIdx,
		@AuthenticationPrincipal JwtAuthentication auth) {
		if (auth == null) {
			throw new NotAuthenticationException("로그인이 필요합니다.");
		}

		return new ApiResponse<>(memberService.getFollowers(auth.id(), username, lastIdx));
	}

	@Operation(
		summary = "팔로우한 목록 조회",
		description = "자기 자신 또는 타인의 팔로잉 목록을 조회합니다.",
		responses = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "팔로잉한 사용자 목록 조회 성공"),
		}
	)
	@GetMapping("/{username}/followings")
	public ApiResponse<List<MemberResponse.FollowingResponse>> getAdditionalFollowings(
		@Parameter(
			name = "사용자 username", description = "해당 사용자의 username 을 입력합니다.", in = ParameterIn.PATH, required = true
		) @PathVariable String username,
		@Parameter(
			name = "사용자 id[cursor]", description = "사용자에게 보여준 목록 중 마지막 memberId[커서]", in = ParameterIn.PATH, required = true
		) @Valid @RequestParam @NotNull Long lastIdx,
		@AuthenticationPrincipal JwtAuthentication auth) {
		if (auth == null) {
			throw new NotAuthenticationException("로그인이 필요합니다.");
		}

		return new ApiResponse<>(this.memberService.getFollowings(auth.id(), username, lastIdx));
	}

}
