package com.kdt.instakyuram.member.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kdt.instakyuram.common.ApiResponse;
import com.kdt.instakyuram.member.domain.Member;
import com.kdt.instakyuram.member.dto.MemberRequest;
import com.kdt.instakyuram.member.dto.MemberResponse;
import com.kdt.instakyuram.member.service.MemberService;
import com.kdt.instakyuram.member.service.ProfileService;
import com.kdt.instakyuram.post.service.PostGiver;
import com.kdt.instakyuram.security.Role;
import com.kdt.instakyuram.security.SecurityConfigProperties;
import com.kdt.instakyuram.security.SecurityConfigProperties.JwtConfigure;
import com.kdt.instakyuram.security.WebSecurityConfig;
import com.kdt.instakyuram.security.jwt.Jwt;
import com.kdt.instakyuram.security.jwt.JwtAuthentication;
import com.kdt.instakyuram.security.jwt.JwtAuthenticationToken;
import com.kdt.instakyuram.token.service.TokenService;

@WebMvcTest({MemberRestController.class, WebSecurityConfig.class})
@EnableConfigurationProperties(SecurityConfigProperties.class)
class MemberRestControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@MockBean
	private MemberService memberService;

	@MockBean
	PostGiver postGiver;

	@MockBean
	ProfileService profileService;

	@Autowired
	SecurityConfigProperties securityConfiguresProperties;

	@MockBean
	TokenService tokenService;

	@MockBean
	Jwt jwt;

	Member member = new Member(1L, "pjh123", "홍길동", "encodedPassword", "01012345678", "user@gmail.com", "");

	@Test
	@DisplayName("Sign in 성공 테스트")
	void testSigninSuccess() throws Exception {
		//given
		String accessToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwicm9sZXMiOlsiTUVNQkVSIl0sImlzcyI6InByZ3JtcyIsImV4cCI6MTY1NTk2ODQ1NCwiaWF0IjoxNjU1OTY4Mzk0LCJtZW1iZXJJZCI6IjEifQ.PBIdS5PThuUD67kCC6kA9ORWi_NB8Izw123XuC6v0pXxCBHOr-wSDcdyKSt734Jsm1Q1rvnKLGDxmTD7etosWA";
		String refreshToken = "refreshToken";
		MemberRequest.SigninRequest signinRequest = new MemberRequest.SigninRequest(
			"pjh123",
			"123456789"
		);
		MemberResponse.SigninResponse signinResponse = new MemberResponse.SigninResponse(
			member.getId(),
			member.getUsername(),
			accessToken,
			refreshToken,
			new String[] {String.valueOf(Role.MEMBER)}
		);
		Jwt.Claims claim = Jwt.Claims.builder().memberId(1L)
			.roles(signinResponse.roles())
			.iat(new Date())
			.exp(new Date()
			).build();

		String request = objectMapper.writeValueAsString(signinRequest);
		String response = objectMapper.writeValueAsString(new ApiResponse<>(signinResponse));

		given(jwt.refreshTokenProperties()).willReturn(this.securityConfiguresProperties.jwt().refreshToken());
		given(jwt.accessTokenProperties()).willReturn(this.securityConfiguresProperties.jwt().accessToken());
		given(jwt.verify(accessToken)).willReturn(claim);
		given(memberService.signin(any(String.class), any(String.class))).willReturn(signinResponse);

		//when
		ResultActions perform = mockMvc.perform(post("/api/members/signin")
			.content(request)
			.contentType(MediaType.APPLICATION_JSON)
		).andDo(print());

		//then
		verify(memberService, times(1)).signin(any(String.class), any(String.class));
		verify(jwt, times(1)).verify(any());

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

	@Test
	@DisplayName("팔로우한 목록을 무한 스크롤로 데이터를 가져온다. [추가로 데이터가 없다면 가져오지 않는다.]")
	void testGetLookUpMyFollower() throws Exception {
		//given
		Long authId = 99L;
		String username = "programmers";
		Long basicIdx = 30L;

		setMockingAuthentication(authId);

		//when
		ResultActions perform = mockMvc.perform(get("/api/members/" + username + "/followings?lastIdx=" + basicIdx)
		).andDo(print());

		//then
		perform.andExpect(status().isOk());

		verify(memberService, times(1)).getFollowings(authId, username, basicIdx);
	}

	@Test
	@DisplayName("팔로잉한 목록을 조회한다.")
	void testGetLookUpFollowings() throws Exception {
		//given
		Long authId = 99L;
		String username = "programmers";
		Long basicIdx = 30L;

		setMockingAuthentication(authId);

		//when
		ResultActions perform = mockMvc.perform(get("/api/members/" + username + "/followings?lastIdx=" + basicIdx)
		).andDo(print());

		//then
		perform.andExpect(status().isOk());

		verify(memberService, times(1)).getFollowings(authId, username, basicIdx);
	}

	@WithMockUser
	@Nested
	@DisplayName("사용자가 팔로잉/팔로워에 대한 추가적인 정보를 위해 lastIdx(cursor) 값을 ")
	class Validation {

		@Test
		@DisplayName("팔로워하는 목록에 대한 추가적으로 데이터를 요청할 떄 lastIdx(cursor) 가 null 일 경우")
		void testGetAdditionalFollowers() throws Exception {
			//given
			String username = "programmers";

			//when
			ResultActions perform = mockMvc.perform(get("/api/members/" + username + "/followers?lastIdx=")
			).andDo(print());

			//then
			perform.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("팔로잉하는 목록에 대한 추가적으로 데이터를 요청할 떄 lastIdx(cursor) 가 null 일 경우")
		void testGetAdditionalFollowings() throws Exception {
			//given
			String username = "programmers";

			//when
			ResultActions perform = mockMvc.perform(get("/api/members/" + username + "/followings?lastIdx=")
			).andDo(print());

			//then
			perform.andExpect(status().isBadRequest());
		}

	}

	private void setMockingAuthentication(Long authId) {
		SimpleGrantedAuthority role_anonymous = new SimpleGrantedAuthority("ROLE_MEMBER");
		List<GrantedAuthority> authorities = new ArrayList<>();
		authorities.add(role_anonymous);
		JwtAuthentication jwtAuthentication = new JwtAuthentication("random-token", authId, "programmers");
		Authentication authentication = new JwtAuthenticationToken(jwtAuthentication, "anonymous", authorities);

		SecurityContext context = SecurityContextHolder.createEmptyContext();
		context.setAuthentication(authentication);
		SecurityContextHolder.setContext(context);
	}
}