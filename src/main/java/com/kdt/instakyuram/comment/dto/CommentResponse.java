package com.kdt.instakyuram.comment.dto;

import com.kdt.instakyuram.member.dto.MemberResponse;
import com.kdt.instakyuram.post.dto.PostResponse;

import lombok.Builder;

@Builder
public record CommentResponse(Long id, String content, PostResponse postResponse, MemberResponse member) {
}
