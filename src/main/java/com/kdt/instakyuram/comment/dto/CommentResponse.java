package com.kdt.instakyuram.comment.dto;

import com.kdt.instakyuram.member.dto.MemberResponse;

import lombok.Builder;

@Builder
public record CommentResponse(Long id, String content, MemberResponse member) {

	public record LikeResponse(
		Long id,
		int likes,
		boolean isLiked
	) {}
}
