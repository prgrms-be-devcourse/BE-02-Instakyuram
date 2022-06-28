package com.kdt.instakyuram.user.member.controller;

import org.springframework.core.io.FileSystemResource;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.kdt.instakyuram.common.ApiResponse;
import com.kdt.instakyuram.user.member.service.ProfileService;
import com.kdt.instakyuram.security.jwt.JwtAuthentication;

@RestController
@RequestMapping("/api/profiles")
public class ProfileRestController {

	private final ProfileService profileService;

	public ProfileRestController(ProfileService profileService) {
		this.profileService = profileService;
	}

	@GetMapping("/{memberId}/image")
	public FileSystemResource get(@PathVariable("memberId") Long id) {
		return this.profileService.findProfileImage(id);
	}

	@PutMapping("/image")
	public ApiResponse<String> update(@AuthenticationPrincipal JwtAuthentication principal,
		MultipartFile profileImage) {
		return new ApiResponse<>(this.profileService.updateProfileImage(principal.id(), profileImage));
	}
}
