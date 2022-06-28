package com.kdt.instakyuram.user.follow.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kdt.instakyuram.user.follow.controller.FollowRestController;
import com.kdt.instakyuram.user.follow.service.FollowService;
import com.kdt.instakyuram.security.jwt.JwtAuthentication;
import com.kdt.instakyuram.security.jwt.JwtAuthenticationToken;

@WebMvcTest(FollowRestController.class)
class FollowRestControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper mapper;

	@MockBean
	private FollowService followService;

	@Test
	@DisplayName("팔로우가 가능 할 경우 응답받기 : true")
	void testPossibleFollow() throws Exception {
		//given
		boolean possible = true;
		Long authId = 1L;
		Long memberId = 2L;

		setMockingAuthentication(authId);

		given(followService.isFollowed(authId, memberId)).willReturn(possible);

		//when
		ResultActions perform = mockMvc.perform(get("/api/friendships/" + memberId))
			.andDo(print());

		//then
		perform.andExpect(status().isOk());

		String jsonResponse = perform.andReturn().getResponse().getContentAsString();
		assertThat(jsonResponse).contains(String.valueOf(possible));
	}

	@Test
	@DisplayName("팔로우가 불가능 할 경우 응답받기 : false")
	void testNotPossibleFollow() throws Exception {
		//given
		boolean impossible = false;
		Long authId = 1L;
		Long followingId = 2L;

		setMockingAuthentication(authId);

		given(followService.isFollowed(authId, followingId)).willReturn(impossible);

		//when
		ResultActions perform = mockMvc.perform(get("/api/friendships/" + followingId))
			.andDo(print());

		//then
		perform.andExpect(status().isOk());

		String jsonResponse = perform.andReturn().getResponse().getContentAsString();
		assertThat(jsonResponse).contains(String.valueOf(impossible));
	}

	@Test
	@DisplayName("팔로우 테스트")
	void testFollow() throws Exception {
		//given
		Long authId = 1L;
		Long targetId = 2L;

		setMockingAuthentication(authId);

		//when
		ResultActions perform = mockMvc.perform(get("/api/friendships/follow/" + targetId))
			.andDo(print());

		//then
		perform.andExpect(status().isOk());
		String response = perform.andReturn()
			.getResponse()
			.getContentAsString();

		assertThat(response).contains("follow");

		verify(followService, times(1)).follow(authId, targetId);
	}

	@Test
	@DisplayName("언팔로우 테스트")
	void testUnFollow() throws Exception {
		//given
		Long authId = 1L;
		Long targetId = 2L;

		setMockingAuthentication(authId);

		//when
		ResultActions perform = mockMvc.perform(get("/api/friendships/unfollow/" + targetId))
			.andDo(print());

		//then
		perform.andExpect(status().isOk());
		String response = perform.andReturn()
			.getResponse()
			.getContentAsString();

		assertThat(response).contains("unfollow");

		verify(followService, times(1)).unFollow(authId, targetId);
	}

	/**
	 * note : 기존에 통합테스트에서 제공했던 것들이 @AuthenticationPrincipal 이 붙으면서 JwtAuthentication 토근으로 변환해야한다.
	 * resolve : 통합 테스트에서는 Application Context 환경에 실행 환경과 동일하기 때문에
	 */
	private void setMockingAuthentication(Long authId) {
		SimpleGrantedAuthority role_anonymous = new SimpleGrantedAuthority("ROLE_MEMBER");
		List<GrantedAuthority> authorities = new ArrayList<>();
		authorities.add(role_anonymous);
		JwtAuthentication jwtAuthentication = new JwtAuthentication("random-token", authId, "anonymous");
		Authentication authentication = new JwtAuthenticationToken(jwtAuthentication, "anonymous", authorities);

		SecurityContext context = SecurityContextHolder.createEmptyContext();
		context.setAuthentication(authentication);
		SecurityContextHolder.setContext(context);
	}
}