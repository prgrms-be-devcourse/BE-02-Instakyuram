package com.kdt.instakyuram.comment.dto;

public record CommentRequest() {

	public record CreateRequest(Long postId, String content) {
	}
}
