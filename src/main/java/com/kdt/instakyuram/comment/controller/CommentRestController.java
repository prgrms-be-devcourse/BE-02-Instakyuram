package com.kdt.instakyuram.comment.controller;

import org.springframework.web.bind.annotation.PatchMapping;
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
	public ApiResponse<CommentResponse> create(@RequestBody CommentRequest request) {
		CommentResponse commentResponse = commentService.create(
			request.postId(), request.memberId(), request.content()
		);

		return new ApiResponse<>(commentResponse);
	}

	@PatchMapping("/{id}")
	public ApiResponse<CommentResponse.UpdateResponse> update(
		@PathVariable Long id,
		@RequestBody CommentRequest request
	) {
		CommentResponse.UpdateResponse commentResponse = commentService.update(
			id, request.postId(), request.memberId(), request.content()
		);

		return new ApiResponse<>(commentResponse);
	}
}
