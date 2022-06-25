package com.kdt.instakyuram.follow;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

import com.kdt.instakyuram.follow.domain.Follow;
import com.kdt.instakyuram.follow.domain.FollowRepository;
import com.kdt.instakyuram.member.domain.Member;
import com.kdt.instakyuram.security.jwt.JwtAuthentication;
import com.kdt.instakyuram.security.jwt.JwtAuthenticationToken;

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
	@DisplayName("사용자가 팔로우 할 수 있는 대상이였을 떄 true 를 반환한다. ")
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
	@DisplayName("사용자가 이미 팔로우 해 할 수 없는 대상일 때 false 를 반환한다. ")
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
	@DisplayName("팔로잉을 한다.")
	@Transactional
	void testFollow() throws Exception {
		//given
		List<Member> members = getMembers();

		Member auth = members.get(0);
		Member target = members.get(1);

		setMockingAuthentication(auth.getId());

		//when
		ResultActions perform = mockMvc.perform(get("/api/friendships/follow/" + target.getId()))
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
	@DisplayName("이미 팔로잉한 상태면 DB 제약조건 오류가 발생한다.")
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
		ResultActions perform = mockMvc.perform(get("/api/friendships/follow/" + target.getId()))
			.andDo(print());

		//then
		perform.andExpect(status().isBadRequest());
	}

	@Test
	@Transactional
	@DisplayName("언팔로우 한다.")
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
		ResultActions perform = mockMvc.perform(get("/api/friendships/unfollow/" + target.getId()))
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
	@DisplayName("팔로우가 안되어 있는데 언팔로우를 한다면 오류가 발생한다.")
	void testFailUnFollow() throws Exception {
		//given
		List<Member> members = getMembers();

		Member auth = members.get(0);
		Member target = members.get(1);

		setMockingAuthentication(auth.getId());

		//when
		ResultActions perform = mockMvc.perform(get("/api/friendships/unfollow/" + target.getId()))
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

	/**
	 * note : 기존에 통합테스트에서 제공했던 것들이 @AuthenticationPrincipal 이 붙으면서 JwtAuthentication 토근으로 변환해야한다.
	 * resolve : 통합 테스트에서는 Application Context 환경에 실행 환경과 동일하기 때문에
	 */
	private void setMockingAuthentication(Long authId) {
		SimpleGrantedAuthority role_anonymous = new SimpleGrantedAuthority("ROLE_MEMBER");
		List<GrantedAuthority> authorities = new ArrayList<>();
		authorities.add(role_anonymous);
		JwtAuthentication jwtAuthentication = new JwtAuthentication("random-token", authId);
		Authentication authentication = new JwtAuthenticationToken(jwtAuthentication, "anonymous", authorities);

		SecurityContext context = SecurityContextHolder.createEmptyContext();
		context.setAuthentication(authentication);
		SecurityContextHolder.setContext(context);
	}
}
