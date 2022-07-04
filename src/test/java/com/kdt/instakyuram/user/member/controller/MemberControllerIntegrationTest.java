package com.kdt.instakyuram.user.member.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import javax.persistence.EntityManager;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kdt.instakyuram.common.PageDto;
import com.kdt.instakyuram.security.jwt.JwtAuthentication;
import com.kdt.instakyuram.security.jwt.JwtAuthenticationToken;
import com.kdt.instakyuram.user.follow.domain.Follow;
import com.kdt.instakyuram.user.member.domain.Member;
import com.kdt.instakyuram.user.member.dto.MemberResponse;

@AutoConfigureMockMvc
@SpringBootTest
public class MemberControllerIntegrationTest {

	@Autowired
	private EntityManager entityManager;
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper mapper;

	@Test
	@Transactional
	@DisplayName("사용자가 목록을 조회한다.")
	void testGetMembers() throws Exception {
		//given
		Long authId = 1L;
		setMockingAuthentication(authId);
		int requestPage = 2;
		int requestSize = 5;
		String htmlTittleContent = "사용자가 팔로잉 할 대상 찾는 사용자 목록";

		PageDto.Request request = new PageDto.Request(requestPage, requestSize);
		Pageable pageRequest = new PageDto.Request(requestPage, requestSize).getPageable(Sort.by("id"));
		List<Member> members = getMembersForOffSetPaging();
		PageImpl<Member> pagingMembers = new PageImpl<>(members, pageRequest, members.size());
		PageDto.Response<MemberResponse.MemberListViewResponse, Member> pageResponse = new PageDto.Response<>(
			pagingMembers,
			member -> new MemberResponse.MemberListViewResponse(member.getId(), member.getUsername(), member.getName(),
				true)
		);

		//when
		ResultActions perform = mockMvc.perform(
			get("/members?page=" + request.page() + "&size=" + request.size())
		);

		//then
		perform.andExpect(status().isOk()).andReturn();
		String htmlContents = perform.andReturn().getResponse().getContentAsString();

		Assertions.assertThat(htmlContents).contains(htmlTittleContent);
	}

	@Test
	@DisplayName("사용자 uri 를 조작할 때, 멤버가 없는 없는 페이지 번호를 요청한다면 오류페이지로 전환한다.")
	void testFailGetMembers() throws Exception {
		// given
		Long authId = 1L;
		setMockingAuthentication(authId);
		int requestPage = 100;
		int requestSize = 5;

		PageDto.Request request = new PageDto.Request(requestPage, requestSize);
		Pageable pageRequest = new PageDto.Request(requestPage, requestSize).getPageable(Sort.by("id"));
		List<Member> members = List.of();
		PageImpl<Member> pagingMembers = new PageImpl<>(members, pageRequest, members.size());
		PageDto.Response<MemberResponse.MemberListViewResponse, Member> pageResponse = new PageDto.Response<>(
			pagingMembers,
			member -> new MemberResponse.MemberListViewResponse(member.getId(), member.getUsername(), member.getName(),
				true)
		);

		//when
		ResultActions perform = mockMvc.perform(
			get("/members?page=" + request.page() + "&size=" + request.size())
		);

		//then
		perform.andExpect(status().isNotFound()).andReturn();
	}

	@Test
	@Transactional
	@DisplayName("팔로우 목록을 조회한다.")
	void testRenderLookUpFollowers() throws Exception {
		//given
		Long authId = 99L;
		setMockingAuthentication(authId);
		List<Member> members = getMembersForCursorPaging();
		Member target = members.remove(0);

		members.forEach(member -> {
			entityManager.persist(
				Follow.builder()
					.targetId(target.getId())
					.memberId(member.getId())
					.build()
			);
		});

		//when
		ResultActions perform = mockMvc.perform(get("/members/" + target.getUsername() + "/followers")
		).andDo(print());

		//then
		perform.andExpect(view().name("modal/follower-list"));
		MockHttpServletResponse response = perform.andReturn().getResponse();
		Assertions.assertThat(response.getContentAsString()).contains("follower-list");
	}

	private List<Member> getMembersForOffSetPaging() {

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

				entityManager.persist(member);
				members.add(member);
			}
		);

		return members;
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

