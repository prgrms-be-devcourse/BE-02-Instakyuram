package com.kdt.instakyuram.post.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kdt.instakyuram.common.ApiResponse;
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
		return new ApiResponse(
			postService.create(
				request.memberId(), request.content(), request.postImages()
			)
		);
	}

	@GetMapping("/{memberId}")
	public ApiResponse<List<PostResponse.FindAllResponse>> findAll(@PathVariable Long memberId) {
		return new ApiResponse(
			postService.findAll(memberId)
		);
	}

}
