package com.kdt.instakyuram.user.member.dto;

import lombok.Builder;

@Builder
public record MemberResponse(
	Long id,
	String username,
	String name,
	String email,
	String phoneNumber,
	String introduction,
	String profileImageName
) {

	public record SignupResponse(
		Long id,
		String username) {
	}

	public record SigninResponse(Long id, String username, String accessToken, String refreshToken, String[] roles) {
	}

	@Builder
	public record MemberListViewResponse(Long id, String username, String name, boolean possibleFollow) {
	}

	public record ProfileInfoResponse(
		Long id,
		String username,
		String name,
		String introduction,
		Long postCount,
		Long followerCount,
		Long followCount,
		String profileImageName
	) {
	}

	@Builder
	public record FollowerResponse(Long id, String username, String name, boolean isAlsoAuthFollowing, boolean isMe) {
	}

	@Builder
	public record FollowingResponse(Long id, String username, String name, boolean isAlsoAuthFollowing, boolean isMe) {
	}

}
