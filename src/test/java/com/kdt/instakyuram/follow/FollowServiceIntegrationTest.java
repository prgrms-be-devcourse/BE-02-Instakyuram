package com.kdt.instakyuram.follow;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.kdt.instakyuram.follow.domain.Follow;
import com.kdt.instakyuram.follow.service.FollowService;
import com.kdt.instakyuram.member.domain.Member;

@SpringBootTest
public class FollowServiceIntegrationTest {

	@Autowired
	EntityManager entityManager;

	@Autowired
	private FollowService followService;

	@Test
	@Transactional
	@DisplayName("팔로잉 목록 조회 테스트")
	void testFollowing() {
		//given
		List<Member> members = getDemoMembers();

		Member member = members.get(0);
		Member targetA = members.get(1);
		Member targetB = members.get(2);

		entityManager.persist(Follow.builder()
			.memberId(member.getId())
			.targetId(targetA.getId())
			.build());

		entityManager.persist(Follow.builder()
			.memberId(member.getId())
			.targetId(targetB.getId())
			.build());

		List<Long> expectedFollowingIds = List.of(targetA.getId(), targetB.getId());

		//when
		List<Long> followingIds = followService.findByFollowingIds(member.getId());

		//then
		assertThat(followingIds.size()).isEqualTo(expectedFollowingIds.size());
		assertThat(followingIds).contains(targetA.getId(), targetB.getId());
	}

	@Transactional
	public List<Member> getDemoMembers() {

		List<Member> follwings = new ArrayList<>();

		String name = "programmers";
		String password = "password";
		String phoneNumber = "01012345678";
		String emailPostfix = "@programmers.co.kr";

		IntStream.range(1, 5).forEach(
			number -> {
				Member member = Member.builder()
					.email((name + number) + emailPostfix)
					.password(password)
					.username(name + number)
					.phoneNumber(phoneNumber)
					.name(name)
					.build();

				entityManager.persist(member);

				follwings.add(member);
			}
		);

		return follwings;
	}

}
