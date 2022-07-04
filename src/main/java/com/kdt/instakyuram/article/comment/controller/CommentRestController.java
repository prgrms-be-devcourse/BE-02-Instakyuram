package com.kdt.instakyuram.article.comment.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kdt.instakyuram.article.comment.dto.CommentFindAllResponse;
import com.kdt.instakyuram.article.comment.dto.CommentRequest;
import com.kdt.instakyuram.article.comment.dto.CommentResponse;
import com.kdt.instakyuram.article.comment.dto.CommentSearch;
import com.kdt.instakyuram.article.comment.service.CommentService;
import com.kdt.instakyuram.common.ApiResponse;
import com.kdt.instakyuram.security.jwt.JwtAuthentication;

@RestController
@RequestMapping("/api/comments")
public class CommentRestController {

	private final CommentService commentService;

	public CommentRestController(CommentService commentService) {
		this.commentService = commentService;
	}

	@PostMapping
	public ApiResponse<CommentResponse> create(
		@AuthenticationPrincipal JwtAuthentication authentication,
		@RequestBody CommentRequest.CreateRequest request
	) {
		CommentResponse commentResponse = commentService.create(
			request.postId(), authentication.id(), request.content()
		);

		return new ApiResponse<>(commentResponse);
	}

	@PatchMapping("/{id}")
	public ApiResponse<CommentResponse> modify(
		@AuthenticationPrincipal JwtAuthentication authentication,
		@PathVariable Long id,
		@RequestBody CommentRequest.ModifyRequest request
	) {
		CommentResponse commentResponse = commentService.modify(
			id, authentication.id(), request.content()
		);

		return new ApiResponse<>(commentResponse);
	}

	@DeleteMapping("/{id}")
	public void delete(
		@AuthenticationPrincipal JwtAuthentication authentication,
		@PathVariable Long id
	) {
		commentService.delete(id, authentication.id());
	}

	@PostMapping("/{id}/like")
	public ApiResponse<CommentResponse.LikeResponse> like(
		@AuthenticationPrincipal JwtAuthentication authentication,
		@PathVariable Long id
	) {
		CommentResponse.LikeResponse commentResponse = commentService.like(
			id, authentication.id()
		);

		return new ApiResponse<>(commentResponse);
	}

	@PostMapping("{id}/unlike")
	public ApiResponse<CommentResponse.LikeResponse> unlike(
		@AuthenticationPrincipal JwtAuthentication authentication,
		@PathVariable Long id
	) {
		CommentResponse.LikeResponse commentResponse = commentService.unlike(
			id, authentication.id()
		);

		return new ApiResponse<>(commentResponse);
	}

	@GetMapping("/post/{postId}")
	public ApiResponse<List<CommentFindAllResponse>> findAll(
		@AuthenticationPrincipal JwtAuthentication authentication,
		@PathVariable Long postId,
		CommentSearch search
	) {
		List<CommentFindAllResponse> comments = commentService.findAll(
			postId,
			authentication.id(),
			search.getId(),
			search.getLimit()
		);

		return new ApiResponse<>(comments);
	}
}
