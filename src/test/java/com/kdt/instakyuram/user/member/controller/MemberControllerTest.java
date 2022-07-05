package com.kdt.instakyuram.user.member.controller;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
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
import com.kdt.instakyuram.article.post.service.PostGiver;
import com.kdt.instakyuram.common.PageDto;
import com.kdt.instakyuram.security.jwt.JwtAuthentication;
import com.kdt.instakyuram.security.jwt.JwtAuthenticationToken;
import com.kdt.instakyuram.user.member.domain.Member;
import com.kdt.instakyuram.user.member.dto.MemberOrderDto;
import com.kdt.instakyuram.user.member.dto.MemberResponse;
import com.kdt.instakyuram.user.member.service.MemberService;
import com.kdt.instakyuram.user.member.service.ProfileService;

@WebMvcTest(MemberController.class)
class MemberControllerTest {

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

	@WithMockUser
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

	@Nested
	@DisplayName("사용자 전체 목록 조회 테스트 ")
	class PagingTest {
		@DisplayName("검색 조건이 없을 때")
		@Test
		void testGetMembers() throws Exception {
			//given
			Long authId = 1L;
			setMockingAuthentication(authId);

			int requestPage = 2;
			int requestSize = 5;
			MemberOrderDto memberOrderDto = new MemberOrderDto(null, null);
			PageDto.Request request = new PageDto.Request(requestPage, requestSize);
			Pageable pageRequest = request.getPageable(memberOrderDto.getSortingCriteria());
			List<Member> members = getMembers();
			PageImpl<Member> pagingMembers = new PageImpl<>(members, pageRequest, members.size());
			PageDto.Response<MemberResponse.MemberListViewResponse, Member> pageResponse = new PageDto.Response<>(
				pagingMembers,
				member -> new MemberResponse.MemberListViewResponse(member.getId(), member.getUsername(),
					member.getName(),
					true)
			);

			given(memberService.findAll(authId, pageRequest)).willReturn(pageResponse);

			//when
			ResultActions perform = mockMvc.perform(
				get("/members?page=" + request.page() + "&size=" + request.size())
			);

			//then
			perform.andExpect(status().isOk())
				.andExpect(view().name("member/member-list"));
		}

		@Nested
		@DisplayName("정렬 조건이")
		class WithSearchDto {
			@DisplayName("username 일 때")
			@Test
			void testGetMembersBySearchDto() throws Exception {
				//given
				Long authId = 1L;
				setMockingAuthentication(authId);

				int requestPage = 2;
				int requestSize = 5;
				MemberOrderDto.SortCondition sortCondition = MemberOrderDto.SortCondition.USERNAME;
				MemberOrderDto memberOrderDto = new MemberOrderDto(sortCondition, null);
				PageDto.Request request = new PageDto.Request(requestPage, requestSize);
				Pageable pageRequest = request.getPageable(memberOrderDto.getSortingCriteria());
				List<Member> members = getMembers().stream()
					.sorted(Comparator.comparing(Member::getUsername))
					.collect(Collectors.toList());
				PageImpl<Member> pagingMembers = new PageImpl<>(members, pageRequest, members.size());
				PageDto.Response<MemberResponse.MemberListViewResponse, Member> pageResponse = new PageDto.Response<>(
					pagingMembers,
					member -> new MemberResponse.MemberListViewResponse(member.getId(), member.getUsername(),
						member.getName(),
						true)
				);

				given(memberService.findAll(authId, pageRequest)).willReturn(pageResponse);

				//when
				ResultActions perform = mockMvc.perform(
					get("/members?page=" + request.page() + "&size=" + request.size() + "&sortCondition="
						+ sortCondition.name())
				);

				//then
				perform.andExpect(status().isOk())
					.andExpect(view().name("member/member-list"));
			}

			@DisplayName("이 없고 정렬 순서만 있을 때 실패한다.")
			@Test
			void testGetMembersByNotValidatedSearchDto() throws Exception {
				//given
				Long authId = 1L;
				setMockingAuthentication(authId);

				int requestPage = 2;
				int requestSize = 5;
				MemberOrderDto.Ordering ordering = MemberOrderDto.Ordering.DESC;
				MemberOrderDto memberOrderDto = new MemberOrderDto(null, ordering);
				PageDto.Request request = new PageDto.Request(requestPage, requestSize);
				Pageable pageRequest = request.getPageable(memberOrderDto.getSortingCriteria());
				List<Member> members = getMembers().stream()
					.sorted(Comparator.comparing(Member::getUsername))
					.collect(Collectors.toList());
				PageImpl<Member> pagingMembers = new PageImpl<>(members, pageRequest, members.size());
				PageDto.Response<MemberResponse.MemberListViewResponse, Member> pageResponse = new PageDto.Response<>(
					pagingMembers,
					member -> new MemberResponse.MemberListViewResponse(member.getId(), member.getUsername(),
						member.getName(),
						true)
				);

				given(memberService.findAll(authId, pageRequest)).willReturn(pageResponse);

				//when
				ResultActions perform = mockMvc.perform(
					get("/members?page=" + request.page() + "&size=" + request.size() // + "&sortCondition=null"
						+ "&ordering=" + ordering.name()
					)
				);

				//then
				perform.andExpect(status().isBadRequest());
			}

		}

	}

	@WithMockUser
	@DisplayName("클라이언트가 보낸 pageDto 의")
	@Nested
	class PageRequest {

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

	@Test
	@DisplayName("팔로우한 목록을 조회한다.")
	void testRenderLookUpFollowers() throws Exception {
		//given
		Long authId = 99L;
		String username = "programmers";
		setMockingAuthentication(authId);

		//when
		ResultActions perform = mockMvc.perform(get("/members/" + username + "/followers")
		).andDo(print());

		//then
		perform.andExpect(status().isOk())
			.andExpect(view().name("modal/follower-list"));

		verify(memberService, times(1)).getFollowers(authId, username, 0L);
	}

	@Test
	@DisplayName("팔로잉한 목록을 조회한다.")
	void testRenderLookUpFollowings() throws Exception {
		//given
		Long authId = 99L;
		String username = "programmers";
		setMockingAuthentication(authId);

		//when
		ResultActions perform = mockMvc.perform(get("/members/" + username + "/followings")
		).andDo(print());

		//then
		perform.andExpect(status().isOk())
			.andExpect(view().name("modal/following-list"));

		verify(memberService, times(1)).getFollowings(authId, username, 0L);
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