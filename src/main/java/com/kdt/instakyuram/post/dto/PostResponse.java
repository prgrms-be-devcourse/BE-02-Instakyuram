package com.kdt.instakyuram.post.dto;

import com.kdt.instakyuram.member.dto.MemberResponse;

public record PostResponse(Long id, Long memberId, String content) {

	 public record CreateResponse(
		Long id,
		Long memberId,
		String content

	) { }

	public record FindAllResponse(
		String content,
		MemberResponse member
	) { }

}
