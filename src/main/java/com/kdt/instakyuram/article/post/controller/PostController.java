package com.kdt.instakyuram.article.post.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.kdt.instakyuram.security.jwt.JwtAuthentication;

@Controller
public class PostController {

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
}
