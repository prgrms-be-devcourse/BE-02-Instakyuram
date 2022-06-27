package com.kdt.instakyuram.member.dto;

import lombok.Builder;

@Builder
public record MemberResponse(
	Long id,
	String username,
	String name,
	String email,
	String phoneNumber,
	String introduction) {

	public record SignupResponse(
		Long id,
		String username) {
	}

	public record SigninResponse(Long id, String username, String accessToken, String refreshToken, String[] roles) {
	}

	public record MemberListViewResponse(Long id, String username, String name) {
	}

	public record ProfileInfoResponse(
		Long id,
		String username,
		String name,
		String introduction,
		Long postCount,
		Long followerCount,
		Long followCount
	) {
	}

	@Builder
	public record FollowerResponse(Long id, String username, String name, boolean isAlsoAuthFollowing, boolean isMe) {
	}

	@Builder
	public record FollowingResponse(Long id, String username, String name, boolean isAlsoAuthFollowing, boolean isMe) {
	}

}
