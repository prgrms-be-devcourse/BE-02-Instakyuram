package com.kdt.instakyuram.article.comment.dto;

import lombok.Builder;

@Builder
public record CommentFindAllResponse(
	Long id,
	Long postId,
	String content,
	Long memberId,
	String username,
	long likes,
	boolean isLiked
) {
}