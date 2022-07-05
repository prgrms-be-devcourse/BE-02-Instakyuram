package com.kdt.instakyuram.user.member.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public record MemberRequest() {
	public record SignUpRequest(
		@Size(min = 6, max = 24)
		@Pattern(regexp = "^[a-z0-9_]*$")
		@NotBlank
		String username,

		@NotBlank
		@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$")
		String password,

		@Size(min = 2, max = 16)
		@NotBlank
		String name,

		@Email
		@NotBlank
		String email,

		@Size(min = 11, max = 11)
		@NotBlank
		String phoneNumber) {
	}

	public record SignInRequest(@NotBlank String username, @NotBlank String password) {
	}
}