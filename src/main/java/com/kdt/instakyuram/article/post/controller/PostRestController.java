package com.kdt.instakyuram.article.post.controller;

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
import com.kdt.instakyuram.article.postimage.dto.PostImageResponse;
import com.kdt.instakyuram.article.post.dto.PostLikeResponse;
import com.kdt.instakyuram.article.post.dto.PostRequest;
import com.kdt.instakyuram.article.post.dto.PostResponse;
import com.kdt.instakyuram.article.post.service.PostService;
import com.kdt.instakyuram.security.jwt.JwtAuthentication;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Post의 api")
@RestController
@RequestMapping("/api/posts")
public class PostRestController {

	private final PostService postService;

	public PostRestController(PostService postService) {
		this.postService = postService;
	}

	@Operation(summary = "post를 등록", description = "작성자 id 와 content, images를 받아서 post를 등록할 수 있습니다.")
	@PostMapping
	public ApiResponse<PostResponse.CreateResponse> posting(
		@Valid PostRequest.CreateRequest request,
		@AuthenticationPrincipal JwtAuthentication jwtAuthentication) {
		return new ApiResponse<>(postService.create(
			jwtAuthentication.id(), request.content(), request.postImages()
		));
	}

	@Operation(
		summary = "follow 하는 멤버와 나의 게시글 조회",
		description = "사용자 id를 통해 사용자가 follow 하는 사람들과 사용자 게시글을 모두 조회할 수 있습니다."
	)
	@GetMapping("/{memberId}")
	public ApiResponse<List<PostResponse.FindAllResponse>> findAll(
		@PathVariable Long memberId) {
		return new ApiResponse<>(postService.findAllRelated(memberId));
	}

	@Operation(summary = "post 수정", description = "post의 content를 수정합니다.")
	@PatchMapping("/{id}")
	public ApiResponse<PostResponse.UpdateResponse> update(
		@PathVariable Long id,
		@AuthenticationPrincipal JwtAuthentication jwtAuthentication,
		@RequestBody PostRequest.UpdateRequest request) {
		return new ApiResponse<>(postService.update(id, jwtAuthentication.id(), request.content()));
	}

	@Operation(summary = "post 삭제", description = "post를 삭제합니다.")
	@DeleteMapping("/{id}")
	public ApiResponse<Long> delete(
		@PathVariable Long id,
		@AuthenticationPrincipal JwtAuthentication jwtAuthentication) {
		return new ApiResponse<>(postService.delete(id, jwtAuthentication.id()));
	}

	@Operation(summary = "post 좋아요", description = "post 좋아요를 누릅니다.")
	@PostMapping("/{id}/like")
	public ApiResponse<PostLikeResponse> like(
		@PathVariable Long id,
		@AuthenticationPrincipal JwtAuthentication jwtAuthentication) {
		return new ApiResponse<>(postService.like(id, jwtAuthentication.id()));
	}

	@Operation(summary = "post 좋아요 취소", description = "post 좋아요를 취소합니다.")
	@DeleteMapping("/{id}/unlike")
	public ApiResponse<PostLikeResponse> unlike(
		@PathVariable Long id,
		@AuthenticationPrincipal JwtAuthentication jwtAuthentication) {
		return new ApiResponse<>(postService.unlike(id, jwtAuthentication.id()));
	}

	@Operation(summary = "post 이미지 조회", description = "post 이미지를 조회합니다.")
	@GetMapping("/{id}/image/{serverFileName}")
	public FileSystemResource getImage(
		@PathVariable Long id,
		@PathVariable String serverFileName) {
		return postService.findImage(id, serverFileName);
	}

	@Operation(summary = "post 썸네일 이미지 정보 조회", description = "post 썸네일 이미지의 정보를 조회합니다.")
	@GetMapping("/thumbnails")
	public ApiResponse<List<PostImageResponse.ThumbnailResponse>> getThumbnails(
		@RequestParam String username) {
		return new ApiResponse<>(postService.findPostThumbnailsByUsername(username));
	}

	@PatchMapping("/lock/{id}")
	public ApiResponse<PostResponse.UpdateResponse> lockedUpdate(@PathVariable Long id,
		@AuthenticationPrincipal JwtAuthentication jwtAuthentication,
		@RequestBody PostRequest.UpdateRequest request) {
		return new ApiResponse<>(postService.lockedUpdate(id, jwtAuthentication.id(), request.content()));
	}
}
