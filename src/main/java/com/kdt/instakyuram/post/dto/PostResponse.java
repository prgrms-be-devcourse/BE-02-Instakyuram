package com.kdt.instakyuram.post.dto;

import java.util.List;

import com.kdt.instakyuram.comment.dto.CommentResponse;
import com.kdt.instakyuram.member.dto.MemberResponse;

import lombok.Builder;

@Builder
public record PostResponse(Long id, MemberResponse memberResponse, String content, List<PostImageResponse> postImageResponse) {

	 public record CreateResponse(
		Long id,
		Long memberId,
		String content

	) { }

	@Builder
	public record FindAllResponse(
		Long id,
		String content,
		MemberResponse member,
		List<PostImageResponse> postImageResponse,
		List<CommentResponse> commentResponse,
		List<PostLikeResponse> postLikeResponse,
		int totalPostLike
	) { }

}
