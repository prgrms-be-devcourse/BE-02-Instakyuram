package com.kdt.instakyuram.member.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.kdt.instakyuram.common.PageDto;
import com.kdt.instakyuram.member.domain.Member;
import com.kdt.instakyuram.member.dto.MemberResponse;
import com.kdt.instakyuram.member.service.MemberService;

@WithMockUser(roles = "MEMBER")
@WebMvcTest(MemberController.class)
class MemberControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper mapper;

	@MockBean
	private MemberService memberService;

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