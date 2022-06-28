package com.kdt.instakyuram.article.post.dto;

import lombok.Builder;

@Builder
public record PostLikeResponse(Long postId, int likes, boolean isLiked) {

}
