package com.kdt.instakyuram.article.comment.dto;

import com.kdt.instakyuram.user.member.dto.MemberResponse;

import lombok.Builder;

@Builder
public record CommentResponse(Long id, Long postId, String content, MemberResponse member) {

	public record LikeResponse(
		Long id,
		int likes,
		boolean isLiked
	) {
	}
}
