package com.kdt.instakyuram.profileimage.controller;

import org.springframework.core.io.FileSystemResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kdt.instakyuram.profileimage.service.ProfileImageService;

@RequestMapping("/api/profileImages")
@RestController
public class ProfileImageRestController {

	private final ProfileImageService profileImageService;

	public ProfileImageRestController(ProfileImageService profileImageService) {
		this.profileImageService = profileImageService;
	}

	@GetMapping("/{memberId}/image/{serverFileName}")
	public FileSystemResource getImage(@PathVariable("memberId") Long id, @PathVariable String serverFileName) {
		return profileImageService.findProfileImage(id, serverFileName);
	}

}
