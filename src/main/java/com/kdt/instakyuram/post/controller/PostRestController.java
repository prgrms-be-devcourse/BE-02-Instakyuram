package com.kdt.instakyuram.post.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.core.io.FileSystemResource;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kdt.instakyuram.common.ApiResponse;
import com.kdt.instakyuram.post.dto.PostImageResponse;
import com.kdt.instakyuram.post.dto.PostLikeResponse;
import com.kdt.instakyuram.post.dto.PostRequest;
import com.kdt.instakyuram.post.dto.PostResponse;
import com.kdt.instakyuram.post.service.PostService;
import com.kdt.instakyuram.security.jwt.JwtAuthentication;

@RestController
@RequestMapping("/api/posts")
public class PostRestController {

	private final PostService postService;

	public PostRestController(PostService postService) {
		this.postService = postService;
	}

	@PostMapping
	public ApiResponse<PostResponse.CreateResponse> posting(@Valid PostRequest.CreateRequest request,
		@AuthenticationPrincipal JwtAuthentication jwtAuthentication) {
		return new ApiResponse<>(postService.create(
			jwtAuthentication.id(), request.content(), request.postImages()
		));
	}

	@GetMapping("/{memberId}")
	public ApiResponse<List<PostResponse.FindAllResponse>> findAll(@PathVariable Long memberId) {
		return new ApiResponse<>(postService.findAllRelated(memberId));
	}

	@PatchMapping("/{id}")
	public ApiResponse<PostResponse.UpdateResponse> update(@PathVariable Long id,
		@AuthenticationPrincipal JwtAuthentication jwtAuthentication,
		@RequestBody PostRequest.UpdateRequest request) {
		return new ApiResponse<>(postService.update(id, jwtAuthentication.id(), request.content()));
	}

	@DeleteMapping("/{id}")
	public ApiResponse<Long> delete(@PathVariable Long id,
		@AuthenticationPrincipal JwtAuthentication jwtAuthentication) {
		return new ApiResponse<>(postService.delete(id, jwtAuthentication.id()));
	}

	@PostMapping("/{id}/like")
	public ApiResponse<PostLikeResponse> like(@PathVariable Long id,
		@AuthenticationPrincipal JwtAuthentication jwtAuthentication) {
		return new ApiResponse<>(postService.like(id, jwtAuthentication.id()));
	}

	@PostMapping("/{id}/unlike")
	public ApiResponse<PostLikeResponse> unlike(@PathVariable Long id,
		@AuthenticationPrincipal JwtAuthentication jwtAuthentication) {
		return new ApiResponse<>(postService.unlike(id, jwtAuthentication.id()));
	}

	@GetMapping("/{id}/image/{serverFileName}")
	public FileSystemResource getImage(@PathVariable Long id, @PathVariable String serverFileName) {
		return postService.findImage(id, serverFileName);
	}

	@GetMapping("/thumbnails")
	public ApiResponse<List<PostImageResponse.ThumbnailResponse>> getThumbnails(
		@RequestParam String username) {
		return new ApiResponse<>(postService.findPostThumbnailsByUsername(username));
	}

}
