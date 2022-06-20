package com.kdt.instakyuram.comment.dto;

public record CommentRequest(Long postId, Long memberId, String content) {

}
