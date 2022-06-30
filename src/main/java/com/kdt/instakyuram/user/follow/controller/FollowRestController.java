package com.kdt.instakyuram.user.follow.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kdt.instakyuram.common.ApiResponse;
import com.kdt.instakyuram.security.jwt.JwtAuthentication;
import com.kdt.instakyuram.user.follow.service.FollowService;

import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;

@Api(tags= "팔로우, 팔로잉 api")
@RestController
@RequestMapping("/api/friendships")
public class FollowRestController {

	private final FollowService followService;

	public FollowRestController(FollowService followService) {
		this.followService = followService;
	}

	@Operation(
		summary = "팔로우 가능 여부 판단",
		description = "팔로우 할 수 있는 대상인지 확인합니다.",
		responses = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "팔로우 가능 여부 true/false"),
		}
	)
	@GetMapping("/{memberId}")
	public ApiResponse<Boolean> isFollowed(
		@AuthenticationPrincipal JwtAuthentication auth,
		@Parameter(
			name = "팔로우 할 대상의 식별 값", description = "사용자 id 값(숫자)을 입력합니다.", in = ParameterIn.PATH, required = true
		) @PathVariable Long memberId) {
		return new ApiResponse<>(followService.isFollowed(auth.id(), memberId));
	}

	@Operation(
		summary = "팔로우",
		description = "해당 사용자를 팔로우 합니다.",
		responses = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "팔로우에 성공하면 'follow' "),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "팔로잉이 이미 되있으면 bad status"),
		}
	)
	@GetMapping("/follow/{memberId}")
	public ApiResponse<String> follow(
		@AuthenticationPrincipal JwtAuthentication auth,
		@Parameter(
			name = "팔로우 대상 식별 값", description = "팔로우 할 사용자 id 값(숫자)을 입력합니다.", in = ParameterIn.PATH, required = true
		) @PathVariable Long memberId) {
		followService.follow(auth.id(), memberId);
		return new ApiResponse<>("follow");
	}

	@Operation(
		summary = "언 팔로우",
		description = "이미 팔로우가 되어있는 사용자를 언팔로우 합니다.",
		responses = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "언 팔로우에 성공하면 'unfollow' "),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "해당 언팔로잉 할 대상이 없거나,팔로잉이 안되었으면 bad status"),
		}
	)
	@GetMapping("/unfollow/{memberId}")
	public ApiResponse<String> unfollow(
		@AuthenticationPrincipal JwtAuthentication auth,
		@Parameter(
			name = "언팔로우 할 대상 식별 값", description = "팔로우 할 사용자 id 값(숫자)을 입력합니다.", in = ParameterIn.PATH, required = true
		) @PathVariable Long memberId) {
		followService.unFollow(auth.id(), memberId);
		return new ApiResponse<>("unfollow");
	}
}
