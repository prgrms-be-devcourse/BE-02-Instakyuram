package com.kdt.instakyuram.post.controller;

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
	public String renderUpload() {
		return "/modal/post-upload";
	}

	@GetMapping("/")
	public String indexPage(@AuthenticationPrincipal JwtAuthentication principal, Model model)
	{
		model.addAttribute("member", principal);
		return "index";
	}
}
