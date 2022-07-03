package com.kdt.instakyuram.user.member.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.kdt.instakyuram.user.member.dto.MemberSearchDto;

@DataJpaTest
class CustomMemberRepositoryImplTest {

	@Autowired
	private MemberRepository memberRepository;

	@Test
	@DisplayName("동적쿼리 + 페이징")
	void testFindAllForPaging() {
		//given
		List<Member> members = getMembers();
		Pageable pageRequest = PageRequest.of(0, 10);

		//when
		MemberSearchDto searchDto = new MemberSearchDto(MemberSearchDto.MemberCondition.valueOf("USERNAME"), null);
		Page<Member> byAllForPaging = memberRepository.findByAllForPaging(members.get(0).getId(), searchDto,
			pageRequest);

		byAllForPaging.getContent().forEach(System.out::println);
		//then

	}

	public List<Member> getMembers() {

		List<Member> members = new ArrayList<>();

		String name = "programmers";
		String password = "devCourse2!";
		String phoneNumber = "01012345678";
		String emailPostfix = "@programmers.co.kr";

		IntStream.rangeClosed(1, 50).forEach(
			number -> {
				Member member = Member.builder()
					.email((name + number) + emailPostfix)
					.password(password)
					.username(name + number)
					.phoneNumber(phoneNumber)
					.name(name)
					.build();

				memberRepository.save(member);
				members.add(member);
			}
		);

		return members;
	}
}