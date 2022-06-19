package com.kdt.instakyuram.post.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PostController {

	@GetMapping("/posts")
	public String postsPage() {
		return "post-list";
	}
}
