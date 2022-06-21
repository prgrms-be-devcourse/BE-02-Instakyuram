package com.kdt.instakyuram.post.dto;

import lombok.Builder;

@Builder
public record PostLikeResponse(Long postId, int likes, boolean isLiked) {

}
