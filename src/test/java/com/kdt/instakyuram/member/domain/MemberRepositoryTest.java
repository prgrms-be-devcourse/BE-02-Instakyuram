package com.kdt.instakyuram.member.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.assertj.core.api.Assertions;
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
		Assertions.assertThat(members.size()).isEqualTo(requestSize);
		Assertions.assertThat(pageMembers.getTotalElements()).isEqualTo(insertedMembers.size());
		Assertions.assertThat(pageMembers.getTotalPages()).isEqualTo(expectedTotalSize);
		Assertions.assertThat(pageable.getOffset()).isEqualTo(expectedOffset);
		Assertions.assertThat(pageable.hasPrevious()).isTrue();
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

}