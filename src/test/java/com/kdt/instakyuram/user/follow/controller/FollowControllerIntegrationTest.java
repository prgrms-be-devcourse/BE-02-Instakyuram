package com.kdt.instakyuram.user.follow.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import com.kdt.instakyuram.security.jwt.JwtAuthentication;
import com.kdt.instakyuram.security.jwt.JwtAuthenticationToken;
import com.kdt.instakyuram.user.follow.domain.Follow;
import com.kdt.instakyuram.user.follow.domain.FollowRepository;
import com.kdt.instakyuram.user.member.domain.Member;

@AutoConfigureMockMvc
@SpringBootTest
public class FollowControllerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private EntityManager entityManager;

	@Autowired
	private FollowRepository followRepository;

	@Test
	@DisplayName("???????????? ????????? ??? ??? ?????? ??????????????? ??? true ??? ????????????. ")
	void testPossibleFollow() throws Exception {
		//given
		Long authId = 1L;
		Long memberId = 2L;
		setMockingAuthentication(authId);

		//when
		ResultActions perform = mockMvc.perform(get("/api/friendships/" + memberId))
			.andDo(print());

		//then
		String response = perform.andExpect(status().isOk())
			.andReturn()
			.getResponse()
			.getContentAsString();

		assertThat(response).contains("true");
	}

	@Test
	@Transactional
	@DisplayName("???????????? ?????? ????????? ??? ??? ??? ?????? ????????? ??? false ??? ????????????. ")
	void testImPossibleFollow() throws Exception {
		//given
		List<Member> members = getMembers();
		Member auth = members.get(0);
		Member target = members.get(1);

		Follow follow = Follow.builder()
			.memberId(auth.getId())
			.targetId(target.getId())
			.build();

		entityManager.persist(follow);

		setMockingAuthentication(auth.getId());

		//when
		ResultActions perform = mockMvc.perform(get("/api/friendships/" + target.getId()))
			.andDo(print());

		//then
		String response = perform.andExpect(status().isOk())
			.andReturn()
			.getResponse()
			.getContentAsString();

		assertThat(response).contains("false");
	}

	@Test
	@DisplayName("???????????? ??????.")
	@Transactional
	void testFollow() throws Exception {
		//given
		List<Member> members = getMembers();

		Member auth = members.get(0);
		Member target = members.get(1);

		setMockingAuthentication(auth.getId());

		//when
		ResultActions perform = mockMvc.perform(post("/api/friendships/follow/" + target.getId()))
			.andDo(print());

		//then
		String response = perform.andExpect(status().isOk())
			.andReturn()
			.getResponse()
			.getContentAsString();

		Assertions.assertThat(response).contains("follow");
	}

	@Test
	@Transactional
	@DisplayName("?????? ???????????? ????????? DB ???????????? ????????? ????????????.")
	void testDuplicateFollow() throws Exception {
		//given
		List<Member> members = getMembers();

		Member auth = members.get(0);
		Member target = members.get(1);

		entityManager.persist(Follow.builder()
			.memberId(auth.getId())
			.targetId(target.getId())
			.build());

		setMockingAuthentication(auth.getId());

		//when
		ResultActions perform = mockMvc.perform(post("/api/friendships/follow/" + target.getId()))
			.andDo(print());

		//then
		perform.andExpect(status().isBadRequest());
	}

	@Test
	@Transactional
	@DisplayName("???????????? ??????.")
	void testUnFollow() throws Exception {
		//given
		List<Member> members = getMembers();

		Member auth = members.get(0);
		Member target = members.get(1);

		entityManager.persist(Follow.builder()
			.memberId(auth.getId())
			.targetId(target.getId())
			.build());

		setMockingAuthentication(auth.getId());

		//when
		ResultActions perform = mockMvc.perform(delete("/api/friendships/unfollow/" + target.getId()))
			.andDo(print());

		//then
		String response = perform.andExpect(status().isOk())
			.andReturn()
			.getResponse()
			.getContentAsString();

		assertThat(response).contains("unfollow");
	}

	@Test
	@Transactional
	@DisplayName("???????????? ????????? ????????? ??????????????? ????????? ????????? ????????????.")
	void testFailUnFollow() throws Exception {
		//given
		List<Member> members = getMembers();

		Member auth = members.get(0);
		Member target = members.get(1);

		setMockingAuthentication(auth.getId());

		//when
		ResultActions perform = mockMvc.perform(delete("/api/friendships/unfollow/" + target.getId()))
			.andDo(print());

		//then
		perform.andExpect(status().isNotFound());
	}

	@Transactional
	public List<Member> getMembers() {

		List<Member> members = new ArrayList<>();

		String name = "programmers";
		String password = "devCourse2!";
		String phoneNumber = "01012345678";
		String emailPostfix = "@programmers.co.kr";

		IntStream.rangeClosed(1, 4).forEach(
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
		JwtAuthentication jwtAuthentication = new JwtAuthentication("random-token", authId, "anonymous");
		Authentication authentication = new JwtAuthenticationToken(jwtAuthentication, "anonymous", authorities);

		SecurityContext context = SecurityContextHolder.createEmptyContext();
		context.setAuthentication(authentication);
		SecurityContextHolder.setContext(context);
	}
}
