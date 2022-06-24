package com.kdt.instakyuram.follow.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kdt.instakyuram.common.ApiResponse;
import com.kdt.instakyuram.follow.service.FollowService;
import com.kdt.instakyuram.security.jwt.JwtAuthentication;

@RestController
@RequestMapping("/api/friendships")
public class FollowRestController {

	private final FollowService followService;

	public FollowRestController(FollowService followService) {
		this.followService = followService;
	}

	@GetMapping("/{memberId}")
	public ApiResponse<Boolean> isFollowed(@AuthenticationPrincipal JwtAuthentication auth,
		@PathVariable Long memberId) {
		return new ApiResponse<>(followService.isFollowed(auth.id(), memberId));
	}

	@GetMapping("/follow/{memberId}")
	public ApiResponse<Boolean> follow(@AuthenticationPrincipal JwtAuthentication auth, @PathVariable Long memberId) {
		return new ApiResponse<>(followService.follow(auth.id(), memberId));
	}

	@GetMapping("/unfollow/{memberId}")
	public ApiResponse<Boolean> unfollow(@AuthenticationPrincipal JwtAuthentication auth, @PathVariable Long memberId) {
		followService.unFollow(auth.id(), memberId);
		return new ApiResponse<>(true);
	}
}
