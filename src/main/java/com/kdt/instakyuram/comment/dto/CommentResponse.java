package com.kdt.instakyuram.comment.dto;

import com.kdt.instakyuram.member.dto.MemberResponse;

public record CommentResponse(Long id, String content, MemberResponse member) {

	public record UpdateResponse(Long id, String content) {}
}
