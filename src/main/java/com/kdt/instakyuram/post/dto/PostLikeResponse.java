package com.kdt.instakyuram.post.dto;

import com.kdt.instakyuram.member.dto.MemberResponse;

import lombok.Builder;

@Builder
public record PostLikeResponse(Long id, PostResponse postResponse, MemberResponse memberResponse) {
}
