package com.kdt.instakyuram.article.post.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.kdt.instakyuram.article.post.dto.PostResponse;
import com.kdt.instakyuram.article.post.service.PostService;
import com.kdt.instakyuram.security.jwt.JwtAuthentication;

@Controller
public class PostController {

	private final PostService postService;

	public PostController(PostService postService) {
		this.postService = postService;
	}

	@GetMapping("/posts")
	public String postsPage() {
		return "post-list";
	}

	@GetMapping("/posts/upload")
	public String renderUpload(@AuthenticationPrincipal JwtAuthentication auth, Model model)
	{
		model.addAttribute("auth", auth);

		return "/modal/post-upload";
	}

	@GetMapping("/posts/details")
	public String postDetails() {
		return "post-details";
	}

	@GetMapping("/")
	public String indexPage(@AuthenticationPrincipal JwtAuthentication auth, Model model)
	{
		model.addAttribute("member", auth);

		return "index";
	}

	@GetMapping("/posts/{id}/update")
	public String postUpdate(
		@PathVariable Long id,
		@AuthenticationPrincipal JwtAuthentication auth, Model model
	) {
		PostResponse.FindAllResponse post = postService.findById(auth.id(), id);
		model.addAttribute("member", auth);
		model.addAttribute("post", post);

		return "/modal/post-update";
	}

}
