package com.kdt.instakyuram.post.dto;

import org.springframework.stereotype.Component;

import com.kdt.instakyuram.member.domain.Member;
import com.kdt.instakyuram.member.dto.MemberResponse;
import com.kdt.instakyuram.post.domain.Post;

@Component
public class PostConverter {

	public Member toMember(MemberResponse memberResponse) {
		return Member.builder()
			.id(memberResponse.id())
			.build();
	}

	public MemberResponse toMemberResponse(Member member) {
		return new MemberResponse(
			member.getId(),
			member.getUsername(),
			member.getName(),
			member.getEmail(),
			member.getPhoneNumber()
		);
	}

	public PostResponse.FindAllResponse toResponse(Post post) {
		return new PostResponse.FindAllResponse(post.getContent(), toMemberResponse(post.getMember()));
	}

}
