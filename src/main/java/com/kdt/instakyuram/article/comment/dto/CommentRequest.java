package com.kdt.instakyuram.article.comment.dto;

public record CommentRequest() {

	public record CreateRequest(Long postId, String content) {
	}

	public record ModifyRequest(String content) {
	}
}
