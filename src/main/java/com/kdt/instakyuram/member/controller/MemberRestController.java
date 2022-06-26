package com.kdt.instakyuram.member.controller;

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

import com.kdt.instakyuram.common.ApiResponse;
import com.kdt.instakyuram.exception.NotAuthenticationException;
import com.kdt.instakyuram.member.dto.MemberRequest;
import com.kdt.instakyuram.member.dto.MemberResponse;
import com.kdt.instakyuram.member.service.MemberService;
import com.kdt.instakyuram.security.jwt.Jwt;
import com.kdt.instakyuram.security.jwt.JwtAuthentication;
import com.kdt.instakyuram.security.jwt.JwtAuthenticationToken;
import com.kdt.instakyuram.security.jwt.JwtConfigure;
import com.kdt.instakyuram.token.service.TokenService;

@RestController
@RequestMapping("/api/members")
public class MemberRestController {

	private final MemberService memberService;
	private final TokenService tokenService;
	private final JwtConfigure jwtConfigure;
	private final Jwt jwt;

	public MemberRestController(MemberService memberService, TokenService tokenService, JwtConfigure jwtConfigure,
		Jwt jwt) {
		this.memberService = memberService;
		this.tokenService = tokenService;
		this.jwtConfigure = jwtConfigure;
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
		ResponseCookie accessTokenCookie = ResponseCookie.from(jwtConfigure.accessToken().header(),
			signinResponse.accessToken()).path("/").build();
		ResponseCookie refreshTokenCookie = ResponseCookie.from(jwtConfigure.refreshToken().header(),
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
	public ApiResponse<String> signout(@AuthenticationPrincipal JwtAuthentication principal, HttpServletRequest request,
		HttpServletResponse response) {
		tokenService.save(principal.token(), principal.id());
		Arrays.stream(request.getCookies())
			.filter(cookie -> cookie.getName().equals(jwtConfigure.refreshToken().header()))
			.findFirst()
			.ifPresent(cookie -> tokenService.deleteByToken(cookie.getValue()));
		Cookie accessTokenCookie = new Cookie(jwtConfigure.accessToken().header(), null);
		Cookie refreshTokenCookie = new Cookie(jwtConfigure.refreshToken().header(), null);
		accessTokenCookie.setMaxAge(0);
		accessTokenCookie.setPath("/");
		refreshTokenCookie.setMaxAge(0);
		refreshTokenCookie.setPath("/");
		response.addCookie(accessTokenCookie);
		response.addCookie(refreshTokenCookie);

		return new ApiResponse<>("signed out");
	}

	@GetMapping("/{username}/followers")
	public ApiResponse<List<MemberResponse.FollowerResponse>> getAdditionalFollowers(@PathVariable String username,
		@Valid @RequestParam @NotNull Long lastIdx,
		@AuthenticationPrincipal JwtAuthentication auth) {
		if (auth == null) {
			throw new NotAuthenticationException("로그인이 필요합니다.");
		}

		return new ApiResponse<>(memberService.getFollowers(auth.id(), username, lastIdx));
	}

	@GetMapping("/{username}/followings")
	public ApiResponse<List<MemberResponse.FollowingResponse>> getAdditionalFollowings(@PathVariable String username,
		@Valid @RequestParam @NotNull Long lastIdx,
		@AuthenticationPrincipal JwtAuthentication auth) {
		if (auth == null) {
			throw new NotAuthenticationException("로그인이 필요합니다.");
		}

		return new ApiResponse<>(memberService.getFollowings(auth.id(), username, lastIdx));
	}

}
