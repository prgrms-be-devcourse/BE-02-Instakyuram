package com.kdt.instakyuram.follow.follow.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.kdt.instakyuram.follow.domain.Follow;
import com.kdt.instakyuram.follow.domain.FollowRepository;
import com.kdt.instakyuram.member.domain.Member;

@DataJpaTest
class FollowRepositoryTest {

	@Autowired
	private TestEntityManager entityManager;

	@Autowired
	private FollowRepository followRepository;

	@Test
	@DisplayName("의존성 주입 테스트")
	void testDependencyInjection() {
		//given
		//when
		//then
		Assertions.assertNotNull(entityManager);
	}

	@Test
	@DisplayName("팔로잉 목록 조회 테스트")
	void testLookUpFollowings() {
		//given
		List<Member> members = getDemoMembers();

		Member followingFromMember = members.get(0);
		Member followingToTargetFirst = members.get(1);
		Member followingToTargetSecond = members.get(2);

		followRepository.save(Follow.builder()
			.memberId(followingFromMember.getId())
			.targetId(followingToTargetFirst.getId())
			.build());

		followRepository.save(Follow.builder()
			.memberId(followingFromMember.getId())
			.targetId(followingToTargetSecond.getId())
			.build());
		//when
		List<Long> followingIds = followRepository.findByMemberId(followingFromMember.getId()).stream()
			.map(Follow::getTargetId)
			.toList();
		//then

		assertThat(followingIds.size()).isEqualTo(2);
		assertThat(followingIds).contains(followingToTargetFirst.getId(), followingToTargetSecond.getId());
	}

	public List<Member> getDemoMembers() {

		List<Member> follwings = new ArrayList<>();

		String name = "programmers";
		String password = "password";
		String phoneNumber = "01012345678";

		IntStream.range(1, 5).forEach(
			number -> {
				String username = name + number;
				String email = username + "@programmers.co.kr";
				Member persistedMember = entityManager.persist(
					Member.builder()
						.email(email)
						.password(password)
						.username(username)
						.phoneNumber(phoneNumber)
						.name(name)
						.build()
				);

				follwings.add(persistedMember);
			}
		);

		return follwings;
	}

}