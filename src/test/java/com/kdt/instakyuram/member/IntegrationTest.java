package com.kdt.instakyuram.member;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.LongStream;

import javax.persistence.EntityManager;

import org.assertj.core.api.Assertions;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.kdt.instakyuram.follow.domain.Follow;
import com.kdt.instakyuram.member.domain.Member;
import com.kdt.instakyuram.member.dto.MemberConverter;
import com.kdt.instakyuram.member.dto.MemberResponse;
import com.kdt.instakyuram.member.service.PostGiver;

@SpringBootTest
public class IntegrationTest {

	@Autowired
	EntityManager entityManager;
	@Autowired
	PostGiver postGiver;

	@Autowired
	MemberConverter memberConverter;

	@Test
	@DisplayName("의존성 테스트")
	void testDependencyInjection() {
		//given
		//when
		//then
		Assertions.assertThat(entityManager).isNotNull();
		Assertions.assertThat(postGiver).isNotNull();
	}

	@Test
	@Transactional
	@DisplayName("팔로잉 목록 멤버를 조회")
	void testFindAllFollowing() {
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

		List<MemberResponse> expectedFollowings = List.of(targetA, targetB).stream()
			.map(memberConverter::toMemberResponse)
			.toList();

		//when
		List<MemberResponse> followings = postGiver.findAllFollowing(member.getId());

		//then
		Assertions.assertThat(followings.size()).isEqualTo(expectedFollowings.size());

		AtomicInteger index = new AtomicInteger();

		followings.forEach(following -> {
			MatcherAssert.assertThat(
				following,
				Matchers.samePropertyValuesAs(expectedFollowings.get(index.getAndIncrement()))
			);
		});
	}

	@Transactional
	public List<Member> getDemoMembers() {

		List<Member> members = new ArrayList<>();

		String name = "programmers";
		String password = "password";
		String phoneNumber = "01012345678";
		String emailPostfix = "@programmers.co.kr";

		LongStream.range(1, 5).forEach(
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
