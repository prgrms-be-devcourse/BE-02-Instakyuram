package com.kdt.instakyuram.user.member.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.kdt.instakyuram.configure.TestJpaAuditConfig;
import com.kdt.instakyuram.user.member.dto.MemberOrderDto;

@Import(TestJpaAuditConfig.class)
@DataJpaTest
class CustomMemberRepositoryImplTest {

	@Autowired
	private MemberRepository memberRepository;

	@Test
	@DisplayName("[동적쿼리] 페이징 정렬 기준 : username")
	void testFindAllForPagingOrderByUsername() {
		//given
		List<Member> members = getMembers();
		int requestPage = 0;
		int requestSize = 10;
		String requestOrder = "username";

		Pageable pageRequest = PageRequest.of(requestPage, requestSize, Sort.by(Sort.Order.by(requestOrder)));
		MemberOrderDto searchDto = new MemberOrderDto(
			MemberOrderDto.SortCondition.valueOf(requestOrder.toUpperCase()), null);
		Long authId = members.get(0).getId();

		//when
		Page<Member> byAllForPaging = memberRepository.findAllExcludeAuth(
			authId,
			searchDto,
			pageRequest);

		List<Member> pagingContents = byAllForPaging.getContent();

		//then
		assertThat(pagingContents.size()).isEqualTo(requestSize);
		pagingContents.forEach(content -> {
			assertThat(content.getUsername()).contains("1");
		});
	}

	@Test
	@DisplayName("[동적쿼리] 페이징 정렬 기준 : updatedAt")
	void testFindAllForPagingOrderByupdatedAt() {
		//given
		List<Member> members = getMembers();
		int requestPage = 0;
		int requestSize = 10;
		String requestOrder = "updatedAt";

		Pageable pageRequest = PageRequest.of(requestPage, requestSize, Sort.by(Sort.Order.by(requestOrder)));
		MemberOrderDto searchDto = new MemberOrderDto(MemberOrderDto.SortCondition.valueOf("UPDATED_AT"), null);
		Long authId = members.get(0).getId();

		//when
		Page<Member> byAllForPaging = memberRepository.findAllExcludeAuth(
			authId,
			searchDto,
			pageRequest);

		List<Member> pagingContents = byAllForPaging.getContent();
		pagingContents.forEach(System.out::println);

		//then
		assertThat(pagingContents.size()).isEqualTo(requestSize);

		AtomicInteger number = new AtomicInteger(2);

		pagingContents.forEach(content -> {
			assertThat(content.getUsername()).contains(String.valueOf(number.getAndIncrement()));
		});
	}

	@Test
	@DisplayName("[동적쿼리] 페이징 정렬 기준 : name")
	void testFindAllForPagingOrderByName() {
		//given
		List<Member> members = getMembers();
		int requestPage = 0;
		int requestSize = 10;
		String requestOrder = "name";

		Pageable pageRequest = PageRequest.of(requestPage, requestSize, Sort.by(Sort.Order.by(requestOrder)));
		MemberOrderDto searchDto = new MemberOrderDto(
			MemberOrderDto.SortCondition.valueOf(requestOrder.toUpperCase()), null);
		Long authId = members.get(0).getId();

		//when
		Page<Member> byAllForPaging = memberRepository.findAllExcludeAuth(
			authId,
			searchDto,
			pageRequest);

		List<Member> pagingContents = byAllForPaging.getContent();
		pagingContents.forEach(System.out::println);

		//then
		assertThat(pagingContents.size()).isEqualTo(requestSize);
		pagingContents.forEach(content -> {
			assertThat(content.getUsername()).contains("1");
		});
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
					.name(name + number)
					.build();

				memberRepository.save(member);
				members.add(member);
			}
		);

		return members;
	}
}