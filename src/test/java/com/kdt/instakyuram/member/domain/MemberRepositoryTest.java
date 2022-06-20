package com.kdt.instakyuram.member.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.samePropertyValuesAs;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@DataJpaTest
class MemberRepositoryTest {

	@Autowired
	private MemberRepository memberRepository;

	@Test
	@DisplayName("팔로잉한 사용자 조회")
	void testFindByIdIn() {
		//given
		List<Member> savedMembers = memberRepository.saveAll(this.getDemoMembers());
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

	@Test
	@DisplayName("멤버 전체 조회 (페이징)")
	void testFindAll() {
		//given
		List<Member> insertedMembers = getMembers();
		memberRepository.saveAll(insertedMembers);

		int requestPage = 2;
		int requestSize = 2;

		PageRequest pageRequest = PageRequest.of(requestPage - 1, requestSize);

		int expectedTotalSize = insertedMembers.size() / requestSize + 1;
		int expectedOffset = (requestPage - 1) * requestSize;

		//when
		Page<Member> pageMembers = memberRepository.findAll(pageRequest);

		List<Member> members = pageMembers.getContent();
		Pageable pageable = pageMembers.getPageable();

		//then
		assertThat(members.size()).isEqualTo(requestSize);
		assertThat(pageMembers.getTotalElements()).isEqualTo(insertedMembers.size());
		assertThat(pageMembers.getTotalPages()).isEqualTo(expectedTotalSize);
		assertThat(pageable.getOffset()).isEqualTo(expectedOffset);
		assertThat(pageable.hasPrevious()).isTrue();
	}

	private List<Member> getMembers() {

		List<Member> members = new ArrayList<>();

		String name = "programmers";
		String password = "devCourse2!";
		String phoneNumber = "01012345678";
		String emailPostfix = "@programmers.co.kr";

		IntStream.rangeClosed(1, 5).forEach(
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

	private List<Member> getDemoMembers() {

		List<Member> followings = new ArrayList<>();

		String name = "programmers";
		String password = "password";
		String phoneNumber = "01012345678";
		String emailPostfix = "@programmers.co.kr";

		LongStream.range(1, 5).forEach(
			number -> followings.add(Member.builder()
				.id(number)
				.email((name + number) + emailPostfix)
				.password(password)
				.username(name + number)
				.phoneNumber(phoneNumber)
				.name(name)
				.build()
			)
		);

		return followings;
	}

}