package com.kdt.instakyuram.user.member.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kdt.instakyuram.common.ApiResponse;
import com.kdt.instakyuram.user.follow.domain.Follow;
import com.kdt.instakyuram.user.member.domain.Member;
import com.kdt.instakyuram.user.member.dto.MemberResponse;
import com.kdt.instakyuram.security.jwt.JwtAuthentication;
import com.kdt.instakyuram.security.jwt.JwtAuthenticationToken;

@AutoConfigureMockMvc
@SpringBootTest
class MemberRestControllerIntegrationTest {

	@Autowired
	private EntityManager entityManager;
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper mapper;

	@Test
	@Transactional
	@DisplayName("팔로워 정보를 추가적으로 요청한다.[cursor]")
	void testGetAdditionalFollowers() throws Exception {
		//given
		Long authId = 99L;
		setMockingAuthentication(authId);
		Long lastIdx = 30L;

		List<Member> members = getMembersForCursorPaging();
		Member target = members.remove(0);

		members.forEach(member -> entityManager.persist(Follow.builder()
			.targetId(target.getId())
			.memberId(member.getId())
			.build())
		);

		//when
		ResultActions perform = mockMvc.perform(
			get("/api/members/" + target.getUsername() + "/followers?lastIdx=" + lastIdx)
		).andDo(print());

		//then
		MvcResult result = perform.andExpect(status().isOk()).andReturn();
		String body = result.getResponse().getContentAsString();
		ApiResponse<List<MemberResponse.FollowerResponse>> listApiResponse = mapper.readValue(
			body, new TypeReference<>() {
			}
		);

		List<MemberResponse.FollowerResponse> responses = listApiResponse.getResponse().stream().toList();
		responses.forEach(followerResponse -> {
			assertThat(followerResponse.id()).isGreaterThan(lastIdx);
		});
	}

	@Test
	@Transactional
	@DisplayName("팔로잉 정보를 추가적으로 요청한다.[cursor]")
	void testGetAdditionalFollowings() throws Exception {
		//given
		Long authId = 99L;
		setMockingAuthentication(authId);
		Long lastIdx = 30L;

		List<Member> members = getMembersForCursorPaging();
		Member target = members.remove(0);

		members.forEach(member -> entityManager.persist(Follow.builder()
			.targetId(member.getId())
			.memberId(target.getId())
			.build())
		);

		//when
		ResultActions perform = mockMvc.perform(
			get("/api/members/" + target.getUsername() + "/followings?lastIdx=" + lastIdx)
		).andDo(print());

		//then
		MvcResult result = perform.andExpect(status().isOk()).andReturn();
		String body = result.getResponse().getContentAsString();
		ApiResponse<List<MemberResponse.FollowingResponse>> listApiResponse = mapper.readValue(
			body, new TypeReference<>() {
			}
		);

		List<MemberResponse.FollowingResponse> responses = listApiResponse.getResponse().stream().toList();
		responses.forEach(followerResponse -> {
			assertThat(followerResponse.id()).isGreaterThan(lastIdx);
		});
	}

	private List<Member> getMembersForCursorPaging() {

		List<Member> members = new ArrayList<>();

		String name = "programmers";
		String password = "devCourse2!";
		String phoneNumber = "01012345678";
		String emailPostfix = "@programmers.co.kr";

		IntStream.rangeClosed(1, 61).forEach(
			number -> {
				Member member = Member.builder()
					.email((name + number) + emailPostfix)
					.password(password)
					.username(name + number)
					.phoneNumber(phoneNumber)
					.name(name)
					.build();

				entityManager.persist(member);
				members.add(member);
			}
		);

		return members;
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
