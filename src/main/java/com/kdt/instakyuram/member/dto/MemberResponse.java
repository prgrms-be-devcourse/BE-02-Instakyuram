package com.kdt.instakyuram.member.dto;

public record MemberResponse(Long id, String username, String name, String email, String phoneNumber) {

	public record FollowingMember(Long id, String username, String name) {

	}
}
