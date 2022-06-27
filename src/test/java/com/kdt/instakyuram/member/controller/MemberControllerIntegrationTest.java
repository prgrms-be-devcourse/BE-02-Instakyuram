package com.kdt.instakyuram.member.controller;

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
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kdt.instakyuram.common.PageDto;
import com.kdt.instakyuram.follow.domain.Follow;
import com.kdt.instakyuram.member.domain.Member;
import com.kdt.instakyuram.member.dto.MemberResponse;
import com.kdt.instakyuram.security.jwt.JwtAuthentication;
import com.kdt.instakyuram.security.jwt.JwtAuthenticationToken;

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
		Long authId=1L;
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
		//then
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
		Long authId=1L;
		setMockingAuthentication(authId);
		int requestPage = 100;
		int requestSize = 5;
		String errorPageTitle = "오류 페이지";

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
		//then
		MvcResult result = mockMvc.perform(
			get("/members?page=" + request.page() + "&size=" + request.size())
		).andExpect(status().isNotFound()).andReturn();
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
		MvcResult result = perform.andExpect(status().isOk())
			.andExpect(view().name("modal/follower-list"))
			.andReturn();

		MockHttpServletResponse response = result.getResponse();
		Assertions.assertThat(response.getContentAsString()).contains("follower-list");
	}

	@Test
	@Transactional
	@DisplayName("팔로잉 목록을 조회한다.")
	void testRenderLookUpFollowings() throws Exception {
		//given
		Long authId = 99L;
		setMockingAuthentication(authId);
		List<Member> members = getMembersForCursorPaging();
		Member target = members.remove(0);

		members.forEach(member -> {
			entityManager.persist(
				Follow.builder()
					.targetId(member.getId())
					.memberId(target.getId())
					.build()
			);
		});

		//when
		ResultActions perform = mockMvc.perform(get("/members/" + target.getUsername() + "/followings")
		).andDo(print());

		//then
		MvcResult result = perform.andExpect(status().isOk())
			.andExpect(view().name("modal/following-list"))
			.andReturn();

		MockHttpServletResponse response = result.getResponse();
		Assertions.assertThat(response.getContentAsString()).contains("following-list");
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

	/**
	 * @deprecated
	 * note : 필요한 데이터를 저장할 때, jpaAudit이 동작하게 된다 (통합테스트에서만)
	 *   @WithMockUser("MEMBER") 사용시 src 내부에 jpaAudit 부분에 castException이 난다.
	 *   만약 임시 데이터를 사용하기 위해서는 해당 메소드를 한번 호출하면 해결된다.
	 * bug : @AutenticationPrincipal이 붙은 Controller 에서는 null이 발생한다. 현재 로직에서는 해당 @AutenticationPrincipal
	 *   이 안들어가기 때문에 JPA Audit부분에서 이슈가 났던것을 단순하게 해결할 수 있었다. 하지만.. @AutenticationPrincipal {@link com.kdt.instakyuram.security.jwt.JwtAuthentication}
	 *   이 붙은 컨트롤러에서는 직접적인 Object Principal 값이 실제로 JwtAutentication 객체가 아니면 null을 반환한다.
	 *   그 이유는 "anonymous" 라는 것이 JwtAuthentication 으로 변경될 수 없기 때문이다. 그러므로 FollowRestControllerTest에 있는 setMockAnonymousAuthenticationToken()을 호출하여 해결할 수 있다.
	 */
	private void setMockAnonymousAuthenticationToken() {
		SimpleGrantedAuthority role_anonymous = new SimpleGrantedAuthority("ROLE_MEMBER");
		List<GrantedAuthority> authorities = new ArrayList<>();
		authorities.add(role_anonymous);
		Authentication authentication = new AnonymousAuthenticationToken("anonymous", "anonymous", authorities);

		SecurityContext context = SecurityContextHolder.createEmptyContext();
		context.setAuthentication(authentication);
		SecurityContextHolder.setContext(context);
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

