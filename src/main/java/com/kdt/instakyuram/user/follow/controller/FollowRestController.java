package com.kdt.instakyuram.user.follow.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kdt.instakyuram.common.ApiResponse;
import com.kdt.instakyuram.security.jwt.JwtAuthentication;
import com.kdt.instakyuram.user.follow.service.FollowService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "팔로우, 팔로잉 api")
@RestController
@RequestMapping("/api/friendships")
public class FollowRestController {

	private final FollowService followService;

	public FollowRestController(FollowService followService) {
		this.followService = followService;
	}

	@Operation(summary = "팔로우 가능 여부 판단", description = "팔로우 할 수 있는 대상인지 확인합니다.")
	@GetMapping("/{memberId}")
	public ApiResponse<Boolean> isFollowed(
		@AuthenticationPrincipal JwtAuthentication auth,
		@PathVariable Long memberId) {
		return new ApiResponse<>(followService.isFollowed(auth.id(), memberId));
	}

	@Operation(summary = "팔로우", description = "해당 사용자를 팔로우 합니다.")
	@GetMapping("/follow/{memberId}")
	public ApiResponse<String> follow(
		@AuthenticationPrincipal JwtAuthentication auth,
		@PathVariable Long memberId) {
		followService.follow(auth.id(), memberId);
		return new ApiResponse<>("follow");
	}

	@Operation(summary = "언 팔로우", description = "이미 팔로우가 되어있는 사용자를 언팔로우 합니다.")
	@GetMapping("/unfollow/{memberId}")
	public ApiResponse<String> unfollow(
		@AuthenticationPrincipal JwtAuthentication auth,
		@PathVariable Long memberId) {
		followService.unFollow(auth.id(), memberId);
		return new ApiResponse<>("unfollow");
	}
}
