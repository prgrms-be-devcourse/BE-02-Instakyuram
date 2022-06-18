package com.kdt.instakyuram.follow.follow.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

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
	@DisplayName("팔로잉 목록 조회 테스트")
	void testLookUpFollowings() {
		//given
		List<Member> members = getDemoMembers();

		Member member = members.get(0);
		Member targetA = members.get(1);
		Member targetB = members.get(2);

		followRepository.save(Follow.builder()
			.memberId(member.getId())
			.targetId(targetA.getId())
			.build());

		followRepository.save(Follow.builder()
			.memberId(member.getId())
			.targetId(targetB.getId())
			.build());

		//when
		List<Long> followingIds = followRepository.findByMemberId(member.getId()).stream()
			.map(Follow::getTargetId)
			.toList();

		//then
		assertThat(followingIds.size()).isEqualTo(2);
		assertThat(followingIds).contains(targetA.getId(), targetB.getId());
	}

	private List<Member> getDemoMembers() {

		List<Member> follwings = new ArrayList<>();

		String name = "programmers";
		String password = "password";
		String phoneNumber = "01012345678";
		String emailPostfix = "@programmers.co.kr";

		IntStream.range(1, 5).forEach(
			number -> {
				Member persistedMember = entityManager.persist(
					Member.builder()
						.email((name + number) + emailPostfix)
						.password(password)
						.username(name + number)
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