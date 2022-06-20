package com.kdt.instakyuram.comment.dto;

public record CommentRequest() {

	public record CreateRequest(Long postId, Long memberId, String content) {}

	public record LikeRequest(Long memberId) {}
}
