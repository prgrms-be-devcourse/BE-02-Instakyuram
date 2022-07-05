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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "회원 api")
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
	ApiResponse<MemberResponse.SignUpResponse> signup(@RequestBody MemberRequest.SignUpRequest request) {
		return new ApiResponse<>(memberService.signUp(request));
	}

	@PostMapping("/signin")
	public ApiResponse<MemberResponse.SignInResponse> signIn(@RequestBody MemberRequest.SignInRequest signInRequest,
		HttpServletRequest request, HttpServletResponse response) {
		MemberResponse.SignInResponse signInResponse = this.memberService.signIn(signInRequest.username(),
			signInRequest.password());
		ResponseCookie accessTokenCookie = ResponseCookie.from(jwt.accessTokenProperties().header(),
			signInResponse.accessToken()).path("/").build();
		ResponseCookie refreshTokenCookie = ResponseCookie.from(jwt.refreshTokenProperties().header(),
			signInResponse.refreshToken()).path("/").build();

		Jwt.Claims claims = jwt.verify(signInResponse.accessToken());
		JwtAuthentication authentication = new JwtAuthentication(signInResponse.accessToken(),
			signInResponse.id(), signInResponse.username());
		JwtAuthenticationToken authenticationToken = new JwtAuthenticationToken(authentication, null,
			jwt.getAuthorities(claims));
		authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
		SecurityContextHolder.getContext().setAuthentication(authenticationToken);
		response.setHeader("Set-Cookie", accessTokenCookie.toString());
		response.addHeader("Set-Cookie", refreshTokenCookie.toString());

		return new ApiResponse<>(signInResponse);
	}

	@PostMapping("/signout")
	public ApiResponse<String> signOut(@AuthenticationPrincipal JwtAuthentication auth, HttpServletRequest request,
		HttpServletResponse response) {
		if (auth == null) {
			throw new NotAuthenticationException("로그인이 필요합니다.");
		}

		Arrays.stream(request.getCookies())
			.filter(cookie -> cookie.getName().equals(jwt.refreshTokenProperties().header()))
			.findFirst()
			.ifPresent(cookie -> this.tokenService.deleteByToken(cookie.getValue()));
		Cookie accessTokenCookie = new Cookie(this.jwt.accessTokenProperties().header(), null);
		Cookie refreshTokenCookie = new Cookie(this.jwt.refreshTokenProperties().header(), null);
		accessTokenCookie.setMaxAge(0);
		accessTokenCookie.setPath("/");
		accessTokenCookie.setMaxAge(this.jwt.accessTokenProperties().expirySeconds());
		accessTokenCookie.setHttpOnly(true);
		refreshTokenCookie.setMaxAge(0);
		refreshTokenCookie.setPath("/");
		refreshTokenCookie.setMaxAge(this.jwt.accessTokenProperties().expirySeconds());
		refreshTokenCookie.setHttpOnly(true);
		response.addCookie(accessTokenCookie);
		response.addCookie(refreshTokenCookie);

		return new ApiResponse<>("signed out");
	}

	@Operation(summary = "해당 사용자의 팔로우 목록 조회", description = "자기 자신 또는 타인의 팔로우 목록을 조회합니다.")
	@GetMapping("/{username}/followers")
	public ApiResponse<List<MemberResponse.FollowerResponse>> getAdditionalFollowers(
		@PathVariable String username,
		@Valid @RequestParam @NotNull Long lastIdx,
		@AuthenticationPrincipal JwtAuthentication auth) {
		if (auth == null) {
			throw new NotAuthenticationException("로그인이 필요합니다.");
		}

		return new ApiResponse<>(memberService.getFollowers(auth.id(), username, lastIdx));
	}

	@Operation(summary = "팔로우한 목록 조회", description = "자기 자신 또는 타인의 팔로잉 목록을 조회합니다.")
	@GetMapping("/{username}/followings")
	public ApiResponse<List<MemberResponse.FollowingResponse>> getAdditionalFollowings(
		@PathVariable String username,
		@Valid @RequestParam @NotNull Long lastIdx,
		@AuthenticationPrincipal JwtAuthentication auth) {
		if (auth == null) {
			throw new NotAuthenticationException("로그인이 필요합니다.");
		}

		return new ApiResponse<>(this.memberService.getFollowings(auth.id(), username, lastIdx));
	}
}
