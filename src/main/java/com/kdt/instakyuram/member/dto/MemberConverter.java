package com.kdt.instakyuram.member.dto;

import org.springframework.stereotype.Component;

import com.kdt.instakyuram.member.domain.Member;

@Component
public class MemberConverter {

	public MemberResponse toMemberResponse(Member following) {
		return MemberResponse.builder()
			.id(following.getId())
			.email(following.getEmail())
			.username(following.getUsername())
			.name(following.getName())
			.phoneNumber(following.getPhoneNumber())
			.build();
	}
}
