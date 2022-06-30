package com.kdt.instakyuram;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api/docs")
@Controller
public class ApiDocsController {

	/**
	 * note : sagger-ui 접근 경로
	 * @return
	 */
	@GetMapping("/swagger")
	public String renderSwaggerPage() {
		return "redirect:/swagger-ui/index.html";
	}
}
