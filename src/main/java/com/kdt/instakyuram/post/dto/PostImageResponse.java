package com.kdt.instakyuram.post.dto;

import lombok.Builder;

@Builder
public record PostImageResponse(Long id, String originalFileName, String serverFileName, Long size, String path) {

	public record DeleteResponse(String serverFileName, String path) {
	}
}
