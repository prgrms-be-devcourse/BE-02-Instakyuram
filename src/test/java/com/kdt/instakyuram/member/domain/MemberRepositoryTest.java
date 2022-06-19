package com.kdt.instakyuram.member.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.samePropertyValuesAs;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.LongStream;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@DataJpaTest
class MemberRepositoryTest {

	@Autowired
	private TestEntityManager entityManager;

	@Autowired
	private MemberRepository memberRepository;

	@Test
	@DisplayName("팔로잉한 사용자 조회")
	void testFindByIdIn() {
		//given
		List<Member> savedMembers = memberRepository.saveAll(this.getDemoMembers());
		Member member = savedMembers.get(0);
		Member targetA = savedMembers.get(1);
		Member targetB = savedMembers.get(2);

		List<Long> followingIds = List.of(targetA.getId(), targetB.getId());
		List<Member> expectedFollowings = List.of(targetA, targetB);

		//when
		List<Member> followings = memberRepository.findByIdIn(followingIds);
		//then

		assertThat(followings.size()).isEqualTo(2);

		AtomicInteger index = new AtomicInteger();

		followings.forEach(following -> {
			MatcherAssert.assertThat(
				following,
				samePropertyValuesAs(expectedFollowings.get(index.getAndIncrement()))
			);
		});
	}

	private List<Member> getDemoMembers() {

		List<Member> follwings = new ArrayList<>();

		String name = "programmers";
		String password = "password";
		String phoneNumber = "01012345678";
		String emailPostfix = "@programmers.co.kr";

		LongStream.range(1, 5).forEach(
			number -> follwings.add(Member.builder()
				.id(number)
				.email((name + number) + emailPostfix)
				.password(password)
				.username(name + number)
				.phoneNumber(phoneNumber)
				.name(name)
				.build()
			)
		);

		return follwings;
	}

}