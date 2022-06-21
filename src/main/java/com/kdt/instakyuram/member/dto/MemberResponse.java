package com.kdt.instakyuram.member.dto;

import lombok.Builder;

@Builder
public record MemberResponse(Long id, String username, String name, String email, String phoneNumber) {
	public record SignupResponse(Long id, String username) {
	}

	public record SigninResponse (Long id, String username, String accessToken, String refreshToken) {
	}

	public record MemberListViewResponse(Long id, String username, String name){
	}
}
