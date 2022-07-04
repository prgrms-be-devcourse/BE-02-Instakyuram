package com.kdt.instakyuram.article.comment.dto;

import java.time.LocalDateTime;

import lombok.Builder;

@Builder
public record CommentFindAllResponse(
	Long id,
	Long postId,
	String content,
	LocalDateTime createdAt,
	Long memberId,
	String username,
	String profileImageName,
	long likes,
	boolean isLiked,
	boolean isUpdated
) {
}