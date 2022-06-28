package com.kdt.instakyuram.user.member.dto;

public record MemberRequest() {
	public record SignupRequest(String username, String password, String name, String email, String phoneNumber) {
	}

	public record SigninRequest(String username, String password) {
	}
}