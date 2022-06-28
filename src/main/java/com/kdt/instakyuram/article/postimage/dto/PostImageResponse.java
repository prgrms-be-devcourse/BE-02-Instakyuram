package com.kdt.instakyuram.article.postimage.dto;

import lombok.Builder;

@Builder
public record PostImageResponse(Long id, Long postId, String originalFileName, String serverFileName, Long size,
								String path) {

	public record DeleteResponse(String serverFileName, String path) {
	}

	public record ThumbnailResponse(Long postId, String serverFileName, String path, Long size) {
	}

}
