package com.kdt.instakyuram.member.dto;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import com.kdt.instakyuram.common.PageDto;
import com.kdt.instakyuram.member.domain.Member;

@Component
public class MemberConverter {

	public PageDto.Response<MemberResponse.MemberListViewResponse, Member> toPageResponse(Page<Member> members) {
		return new PageDto.Response<>(
			members,
			member -> new MemberResponse.MemberListViewResponse(member.getId(), member.getUsername(),
				member.getName()));
	}

	public MemberResponse toMemberResponse(Member member) {
		return MemberResponse.builder()
			.id(member.getId())
			.email(member.getEmail())
			.username(member.getUsername())
			.name(member.getName())
			.phoneNumber(member.getPhoneNumber())
			.introduction(member.getIntroduction())
			.build();
	}

	public MemberResponse.FollowerResponse toFollower(Member member, boolean isAlsoAuthFollowing, boolean isMe) {
		return MemberResponse.FollowerResponse.builder()
			.id(member.getId())
			.username(member.getUsername())
			.name(member.getName())
			.isAlsoAuthFollowing(isAlsoAuthFollowing)
			.isMe(isMe)
			.build();
	}

	public MemberResponse.FollowingResponse toFollowings(Member member, boolean isAlsoAuthFollowing, boolean isMe) {
		return MemberResponse.FollowingResponse.builder()
			.id(member.getId())
			.username(member.getUsername())
			.name(member.getName())
			.isAlsoAuthFollowing(isAlsoAuthFollowing)
			.isMe(isMe)
			.build();
	}
}
