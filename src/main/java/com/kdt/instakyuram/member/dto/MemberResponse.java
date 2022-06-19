package com.kdt.instakyuram.member.dto;

import lombok.Builder;

@Builder
public record MemberResponse(Long id, String username, String name, String email, String phoneNumber) {
	public record SignupResponse (Long id, String username) {
	}

	// todo : 팔로우 할 멤버인데 네이밍 추천좀 ;;;
	public record ViewResponse(Long id, String username, String name){

	}
}
