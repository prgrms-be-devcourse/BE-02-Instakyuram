package com.kdt.instakyuram.post.dto;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public record PostRequest() {

	public record CreateRequest(
		// todo : [IK-203] memberId 가 필요없을 것 같습니당 없애면 말씀 해주세용
		Long memberId,
		String content,
		List<MultipartFile> postImages
	) { }

	public record UpdateRequest(
		Long memberId,
		String content
	) {}

	public record DeleteRequest(Long memberId
	) {
	}
}
