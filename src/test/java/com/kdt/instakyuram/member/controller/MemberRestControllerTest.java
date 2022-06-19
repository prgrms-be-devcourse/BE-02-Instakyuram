package com.kdt.instakyuram.member.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kdt.instakyuram.common.ApiResponse;
import com.kdt.instakyuram.member.domain.Member;
import com.kdt.instakyuram.member.dto.MemberRequest;
import com.kdt.instakyuram.member.dto.MemberResponse;
import com.kdt.instakyuram.member.service.MemberService;
import com.kdt.instakyuram.security.WebSecurityConfigure;
import com.kdt.instakyuram.security.jwt.JwtConfigure;
import com.kdt.instakyuram.token.service.TokenService;

@WebMvcTest({MemberRestController.class, WebSecurityConfigure.class})
@EnableConfigurationProperties(JwtConfigure.class)
class MemberRestControllerTest {

	@Autowired
	MockMvc mockMvc;

	@MockBean
	MemberService memberService;

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	JwtConfigure jwtConfigure;

	@MockBean
	TokenService tokenService;

	Member member = new Member(1L, "pjh123", "홍길동", "encodedPassword", "01012345678", "user@gmail.com");

	@Test
	@DisplayName("Sign in 성공 테스트")
	void testSigninSuccess() throws Exception {
		//given
		String accessToken = "accessToken";
		String refreshToken = "refreshToken";
		MemberRequest.SigninRequest signinRequest = new MemberRequest.SigninRequest(
			"pjh123",
			"123456789"
		);
		MemberResponse.SigninResponse signinResponse = new MemberResponse.SigninResponse(
			member.getId(),
			member.getUsername(),
			accessToken,
			refreshToken
		);

		String request = objectMapper.writeValueAsString(signinRequest);
		String response = objectMapper.writeValueAsString(new ApiResponse<>(signinResponse));

		given(memberService.signin(any(String.class), any(String.class))).willReturn(signinResponse);

		//when
		ResultActions perform = mockMvc.perform(post("/api/members/signin")
			.content(request)
			.contentType(MediaType.APPLICATION_JSON)
		).andDo(print());

		//then
		verify(memberService, times(1)).signin(any(String.class), any(String.class));

		perform
			.andExpect(status().isOk())
			.andExpect(content().string(response));
	}

	@Test
	@DisplayName("sign up 성공 테스트")
	void testSignupSuccess() throws Exception {
		//given
		MemberRequest.SignupRequest signupRequest = new MemberRequest.SignupRequest(
			member.getUsername(),
			"123456789",
			member.getName(),
			member.getEmail(),
			member.getPhoneNumber()
		);
		MemberResponse.SignupResponse signupResponse = new MemberResponse.SignupResponse(
			member.getId(),
			member.getUsername()
		);

		String request = objectMapper.writeValueAsString(signupRequest);
		String response = objectMapper.writeValueAsString(new ApiResponse<>(signupResponse));

		given(memberService.signup(signupRequest)).willReturn(signupResponse);

		//when
		ResultActions perform = mockMvc.perform(post("/api/members/signup")
			.content(request)
			.contentType(MediaType.APPLICATION_JSON)
		).andDo(print());

		//then
		verify(memberService, times(1)).signup(any(MemberRequest.SignupRequest.class));

		perform
			.andExpect(status().isOk())
			.andExpect(content().string(response));
	}
}