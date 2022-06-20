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
			member -> new MemberResponse.MemberListViewResponse(member.getId(), member.getUsername(), member.getName()));
	}
}
