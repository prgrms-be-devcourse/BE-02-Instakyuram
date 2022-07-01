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

import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;

@Api(tags= "사용자 이미지 api")
@RestController
@RequestMapping("/api/profiles")
public class ProfileRestController {

	private final ProfileService profileService;

	public ProfileRestController(ProfileService profileService) {
		this.profileService = profileService;
	}


	@Operation(
		summary = "사용자 이미지 로딩",
		description = "자신 또는 상대방의 이미지 파일을 웹 브라우저에 전달합니다.",
		responses = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "이미지 로딩 성공."),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "이미지 로딩 중 문제 발생."),
		}
	)
	@GetMapping("/{memberId}/image")
	public FileSystemResource get(
		@Parameter(
			name = "이미지를 소유한 사용자 식별 값", description = "사용자 id 값(숫자)을 입력합니다.", in = ParameterIn.PATH, required = true
		)@PathVariable("memberId") Long id) {
		return this.profileService.findProfileImage(id);
	}

	@PutMapping("/image")
	public ApiResponse<String> update(@AuthenticationPrincipal JwtAuthentication principal,
		MultipartFile profileImage) {
		return new ApiResponse<>(this.profileService.updateProfileImage(principal.id(), profileImage));
	}
}
