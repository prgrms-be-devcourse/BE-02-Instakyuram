package com.kdt.instakyuram.comment.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kdt.instakyuram.comment.dto.CommentRequest;
import com.kdt.instakyuram.comment.dto.CommentResponse;
import com.kdt.instakyuram.comment.service.CommentService;
import com.kdt.instakyuram.common.ApiResponse;

@RestController
@RequestMapping("/api/comments")
public class CommentRestController {

	private final CommentService commentService;

	public CommentRestController(CommentService commentService) {
		this.commentService = commentService;
	}

	@PostMapping
	public ApiResponse<CommentResponse> create(@RequestBody CommentRequest.CreateRequest request) {
		CommentResponse commentResponse = commentService.create(
			request.postId(), request.memberId(), request.content()
		);

		return new ApiResponse<>(commentResponse);
	}

	@PostMapping("/{id}/like")
	public ApiResponse<CommentResponse.LikeResponse> like(
		@PathVariable Long id,
		@RequestBody CommentRequest.LikeRequest request
	) {
		CommentResponse.LikeResponse commentResponse = commentService.like(
			id, request.memberId()
		);

		return new ApiResponse<>(commentResponse);
	}
}
