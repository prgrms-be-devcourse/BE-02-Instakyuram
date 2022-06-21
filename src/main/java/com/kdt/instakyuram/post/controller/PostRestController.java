package com.kdt.instakyuram.post.controller;

import java.util.List;

import org.springframework.core.io.FileSystemResource;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kdt.instakyuram.common.ApiResponse;
import com.kdt.instakyuram.post.dto.PostLikeRequest;
import com.kdt.instakyuram.post.dto.PostLikeResponse;
import com.kdt.instakyuram.post.dto.PostRequest;
import com.kdt.instakyuram.post.dto.PostResponse;
import com.kdt.instakyuram.post.service.PostService;

@RestController
@RequestMapping("/api/posts")
public class PostRestController {

	private final PostService postService;

	public PostRestController(PostService postService) {
		this.postService = postService;
	}

	@PostMapping
	public ApiResponse<PostResponse.CreateResponse> posting(
		PostRequest.CreateRequest request
	) {
		return new ApiResponse<>(postService.create(
			request.memberId(), request.content(), request.postImages()
		));
	}

	@GetMapping("/{memberId}")
	public ApiResponse<List<PostResponse.FindAllResponse>> findAll(@PathVariable Long memberId) {
		return new ApiResponse<>(postService.findAll(memberId));
	}

	@PatchMapping("/{id}")
	public ApiResponse<PostResponse.UpdateResponse> update(@PathVariable Long id, @RequestBody PostRequest.UpdateRequest request) {
		return new ApiResponse<>(postService.update(id, request.memberId(), request.content()));
	}

	@DeleteMapping("/{id}")
	public ApiResponse<Long> delete(@PathVariable Long id, @RequestBody PostRequest.DeleteRequest request) {
		return new ApiResponse<>(postService.delete(id, request.memberId()));
	}

	@PostMapping("/{id}/like")
	public ApiResponse<PostLikeResponse> like(@PathVariable Long id, @RequestBody PostLikeRequest postLikeRequest) {
		return new ApiResponse<>(postService.like(id, postLikeRequest.memberId()));
	}

	@PostMapping("/{id}/unlike")
	public ApiResponse<PostLikeResponse> unlike(@PathVariable Long id,
		@RequestBody PostLikeRequest postLikeRequest) {
		return new ApiResponse<>(postService.unlike(id, postLikeRequest.memberId()));
	}

	@GetMapping("/{id}/image/{serverFileName}")
	public FileSystemResource getImage(@PathVariable Long id, @PathVariable String serverFileName)
	{
		return postService.findImage(id, serverFileName);
	}

}
