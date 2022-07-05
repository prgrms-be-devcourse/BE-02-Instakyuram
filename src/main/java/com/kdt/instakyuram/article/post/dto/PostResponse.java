package com.kdt.instakyuram.article.post.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.kdt.instakyuram.article.postimage.dto.PostImageResponse;
import com.kdt.instakyuram.article.comment.dto.CommentResponse;
import com.kdt.instakyuram.user.member.dto.MemberResponse;

import lombok.Builder;

@Builder
public record PostResponse(Long id, MemberResponse memberResponse, String content,
						   List<PostImageResponse> postImageResponse) {

	public record CreateResponse(
		Long id,
		Long memberId,
		String content
	) {
	}

	@Builder
	public record FindAllResponse(
		Long id,
		String content,
		MemberResponse member,
		List<PostImageResponse> postImageResponse,
		List<CommentResponse> commentResponse,
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
		LocalDateTime createdAt,
		int totalPostLike
	) {
	}

	public record UpdateResponse(
		Long id,
		String content
	) {
	}
}
