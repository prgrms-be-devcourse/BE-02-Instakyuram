package com.kdt.instakyuram.member;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kdt.instakyuram.common.PageDto;
import com.kdt.instakyuram.member.domain.Member;
import com.kdt.instakyuram.member.dto.MemberResponse;

@WithMockUser("MEMBER")
@AutoConfigureMockMvc
@SpringBootTest
public class MemberControllerIntegerationTest {

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
		// given
		int requestPage = 2;
		int requestSize = 5;

		PageDto.Request request = new PageDto.Request(requestPage, requestSize);
		Pageable pageRequest = new PageDto.Request(requestPage, requestSize).getPageable(Sort.by("id"));
		List<Member> members = getMembers();
		String htmlTittleContent = "사용자가 팔로잉 할 대상 찾는 사용자 목록";
		PageImpl<Member> pagingMembers = new PageImpl<>(members, pageRequest, members.size());
		PageDto.Response<MemberResponse.ViewResponse, Member> pageResponse = new PageDto.Response<>(
			pagingMembers,
			member -> new MemberResponse.ViewResponse(member.getId(), member.getUsername(), member.getName())
		);
		//when, then
		MvcResult result = mockMvc.perform(
			get("/members?page=" + request.page() + "&size=" + request.size())
		).andExpect(status().isOk()).andReturn();

		String htmlContents = result.getResponse().getContentAsString();

		Assertions.assertThat(htmlContents).contains(htmlTittleContent);
	}

	@Test
	@DisplayName("사용자 uri 를 조작할 때, 멤버가 없는 없는 페이지 번호를 요청한다면 오류페이지로 전환한다.")
	void testFailGetMembers() throws Exception {
		// given
		int requestPage = 100;
		int requestSize = 5;

		PageDto.Request request = new PageDto.Request(requestPage, requestSize);
		Pageable pageRequest = new PageDto.Request(requestPage, requestSize).getPageable(Sort.by("id"));
		List<Member> members = List.of();
		String htmlTittleContent = "사용자가 팔로잉 할 대상 찾는 사용자 목록";
		PageImpl<Member> pagingMembers = new PageImpl<>(members, pageRequest, members.size());
		PageDto.Response<MemberResponse.ViewResponse, Member> pageResponse = new PageDto.Response<>(
			pagingMembers,
			member -> new MemberResponse.ViewResponse(member.getId(), member.getUsername(), member.getName())
		);
		String errorPageTitle = "오류 페이지";
		//when, then
		MvcResult result = mockMvc.perform(
			get("/members?page=" + request.page() + "&size=" + request.size())
		).andExpect(status().isBadRequest()).andReturn();

		String errorHtml = result.getResponse().getContentAsString();
		Assertions.assertThat(errorHtml).contains(errorPageTitle);

	}

	public List<Member> getMembers() {

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
}
