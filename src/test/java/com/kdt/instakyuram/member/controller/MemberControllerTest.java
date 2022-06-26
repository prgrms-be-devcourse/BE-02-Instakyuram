package com.kdt.instakyuram.member.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.IntStream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kdt.instakyuram.common.ApiResponse;
import com.kdt.instakyuram.common.PageDto;
import com.kdt.instakyuram.member.domain.Member;
import com.kdt.instakyuram.member.dto.MemberRequest;
import com.kdt.instakyuram.member.dto.MemberResponse;
import com.kdt.instakyuram.member.service.MemberService;
import com.kdt.instakyuram.member.service.ProfileService;
import com.kdt.instakyuram.post.service.PostGiver;
import com.kdt.instakyuram.security.Role;
import com.kdt.instakyuram.security.SecurityConfigProperties;
import com.kdt.instakyuram.security.WebSecurityConfig;
import com.kdt.instakyuram.security.jwt.Jwt;
import com.kdt.instakyuram.token.service.TokenService;

@WithMockUser(roles = "MEMBER")
@WebMvcTest({MemberRestController.class, MemberController.class, WebSecurityConfig.class})
@EnableConfigurationProperties(SecurityConfigProperties.class)
class MemberControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	SecurityConfigProperties securityConfiguresProperties;

	@Autowired
	ObjectMapper objectMapper;

	@MockBean
	private MemberService memberService;

	@MockBean
	PostGiver postGiver;

	@MockBean
	ProfileService profileService;

	@MockBean
	TokenService tokenService;

	@MockBean
	Jwt jwt;

	Member member = new Member(1L, "pjh123", "홍길동", "encodedPassword", "01012345678", "user@gmail.com", "");

	@Test
	@DisplayName("사용자 전체 목록 조회 최초 진입 테스트")
	void testFirstRequestMembers() throws Exception {
		//given
		String redirectionUri = "/members?page=1&size=10";

		//when
		ResultActions perform = mockMvc.perform(
			get("/members/all")
		);

		//then
		perform.andExpect(redirectedUrl(redirectionUri))
			.andExpect(status().is3xxRedirection());
	}

	@DisplayName("사용자 전체 목록 조회 테스트")
	@Test
	void testGetMembers() throws Exception {
		//given
		int requestPage = 2;
		int requestSize = 5;

		PageDto.Request request = new PageDto.Request(requestPage, requestSize);
		Pageable pageRequest = new PageDto.Request(requestPage, requestSize).getPageable(Sort.by("id"));
		List<Member> members = getMembers();
		PageImpl<Member> pagingMembers = new PageImpl<>(members, pageRequest, members.size());
		PageDto.Response<MemberResponse.MemberListViewResponse, Member> pageResponse = new PageDto.Response<>(
			pagingMembers,
			member -> new MemberResponse.MemberListViewResponse(member.getId(), member.getUsername(), member.getName())
		);

		given(memberService.findAll(any())).willReturn(pageResponse);

		//when
		ResultActions perform = mockMvc.perform(
			get("/members?page=" + request.page() + "&size=" + request.size())
		);

		//then
		perform.andExpect(status().isOk());

		verify(memberService, times(1)).findAll(any());
	}

	@Nested
	class PageRequest {

		@Nested
		@DisplayName("클라이언트가 보낸 pageDto 의")
		class PageDtoValidation {

			@Test
			@DisplayName("음수의 page 번호 값은 400 status 를 반환한다. ")
			void requestMinusSizeValue() throws Exception {
				// given
				PageDto.Request requestDto = new PageDto.Request(-1, 5);

				// when
				// then
				perform(requestDto).andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
			}

			@Test
			@DisplayName("범위에 벗어난 size 값은 400 status 를 반환한다. ")
			void requestSizeLessThan5() throws Exception {
				// given
				PageDto.Request requestDto = new PageDto.Request(2, 4);

				// when
				// then
				perform(requestDto).andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
			}

			@Test
			@DisplayName("범위에 벗어난 size 값은 400 status 를 반환한다. ")
			void requestSizeGraterThan11() throws Exception {
				// given
				PageDto.Request requestDto = new PageDto.Request(2, 11);

				// when
				// then
				perform(requestDto).andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
			}

			@Test
			@DisplayName("page 에 아무런 값이 안들어 올경우 400 status 를 반환한다.")
			void requestPageValueNull() throws Exception {
				// given
				PageDto.Request requestDto = new PageDto.Request(null, 1);

				// when
				// then
				perform(requestDto).andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
			}

			@Test
			@DisplayName("size 에 아무런 값이 안들어 올경우 400 status 를 반환한다.")
			void requestSizeValueNull() throws Exception {
				// given
				PageDto.Request requestDto = new PageDto.Request(3, null);

				// when
				// then
				perform(requestDto).andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
			}

			private ResultActions perform(PageDto.Request request) throws Exception {
				return mockMvc.perform(
					get("/members?page=" + request.page() + "&size=" + request.size())
						.contentType(MediaType.APPLICATION_JSON)
				);
			}

		}
	}

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

	private List<Member> getMembers() {

		List<Member> members = new ArrayList<>();

		String name = "programmers";
		String password = "devCourse2!";
		String phoneNumber = "01012345678";
		String emailPostfix = "@programmers.co.kr";

		IntStream.rangeClosed(1, 25).forEach(
			number -> {
				Member member = Member.builder()
					.email((name + number) + emailPostfix)
					.password(password)
					.username(name + number)
					.phoneNumber(phoneNumber)
					.name(name)
					.build();

				members.add(member);
			}
		);

		return members;
	}
}