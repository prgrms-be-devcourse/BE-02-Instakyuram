package com.kdt.instakyuram.member.service;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import org.assertj.core.api.Assertions;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.kdt.instakyuram.common.PageDto;
import com.kdt.instakyuram.exception.NotFoundException;
import com.kdt.instakyuram.follow.service.FollowService;
import com.kdt.instakyuram.member.domain.Member;
import com.kdt.instakyuram.member.domain.MemberRepository;
import com.kdt.instakyuram.member.dto.MemberConverter;
import com.kdt.instakyuram.member.dto.MemberResponse;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

	@InjectMocks
	private MemberService memberService;

	@Mock
	private MemberRepository memberRepository;

	@Mock
	private MemberConverter memberConverter;

	@Mock
	private FollowService followService;

	@Test
	@DisplayName("사용자 목록 조회")
	void testFindAll() {
		//given
		int requestPage = 2;
		int requestSize = 5;

		Pageable pageRequest = new PageDto.Request(requestPage, requestSize).getPageable(Sort.by("id"));
		List<Member> members = getMembers();
		PageImpl<Member> pagingMembers = new PageImpl<>(members, pageRequest, members.size());
		PageDto.Response<MemberResponse.MemberListViewResponse, Member> pageResponse = new PageDto.Response<>(
			pagingMembers,
			member -> new MemberResponse.MemberListViewResponse(member.getId(), member.getUsername(), member.getName())
		);
		List<Integer> expectedPageNumbers = IntStream.rangeClosed(1, pageResponse.getTotalPage())
			.boxed()
			.toList();

		given(memberRepository.findAll(pageRequest)).willReturn(pagingMembers);
		given(memberConverter.toPageResponse(pagingMembers)).willReturn(pageResponse);

		//when
		PageDto.Response<MemberResponse.MemberListViewResponse, Member> pageMemberResponses = memberService.findAll(pageRequest);

		//then
		verify(memberRepository, times(1)).findAll(pageRequest);
		verify(memberConverter, times(1)).toPageResponse(pagingMembers);

		Assertions.assertThat(pageMemberResponses.getPage()).isEqualTo(requestPage);
		Assertions.assertThat(pageMemberResponses.hasPrevious()).isFalse();
		Assertions.assertThat(pageMemberResponses.hasNext()).isFalse();
		Assertions.assertThat(pageMemberResponses.getPageNumbers().size()).isEqualTo(expectedPageNumbers.size());
	}

	@Test
	@DisplayName("사용자 목록 조회에 데이터가 없는 경우 실패한다")
	void testFailFindAll() {
		//given
		int requestPage = 2;
		int requestSize = 5;

		Pageable pageRequest = new PageDto.Request(requestPage, requestSize).getPageable(Sort.by("id"));
		PageImpl<Member> pagingMembers = new PageImpl<>(List.of(), pageRequest, 0);
		PageDto.Response<MemberResponse.MemberListViewResponse, Member> pageResponse = new PageDto.Response<>(
			pagingMembers,
			member -> new MemberResponse.MemberListViewResponse(member.getId(), member.getUsername(), member.getName())
		);

		given(memberRepository.findAll(pageRequest)).willReturn(pagingMembers);

		//when
		//then
		Assertions.assertThatThrownBy(() -> {
			memberService.findAll(pageRequest);
		}).isInstanceOf(NotFoundException.class);
	}

	private List<Member> getMembers() {

		List<Member> members = new ArrayList<>();

		String name = "programmers";
		String password = "devCourse2!";
		String phoneNumber = "01012345678";
		String emailPostfix = "@programmers.co.kr";

		IntStream.rangeClosed(1, 25).forEach(
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

	@Test
	@DisplayName("팔로잉한 사용자 목록 조회")
	void findAllFollowing() {
		List<Member> members = getDemoMembers();

		Member member = members.get(0);
		Member targetA = members.get(1);
		Member targetB = members.get(2);

		List<Member> followings = List.of(targetA, targetB);
		List<Long> followingIds = followings.stream().map(Member::getId).toList();
		List<MemberResponse> expectedResponses = followings.stream()
			.map(following -> MemberResponse.builder()
				.id(following.getId())
				.email(following.getEmail())
				.username(following.getUsername())
				.name(following.getName())
				.phoneNumber(following.getPhoneNumber())
				.build())
			.toList();

		// given
		given(followService.findByFollowingIds(member.getId())).willReturn(followingIds);
		given(memberRepository.findByIdIn(followingIds)).willReturn(followings);
		given(memberConverter.toMemberResponse(targetA)).willReturn(expectedResponses.get(0));
		given(memberConverter.toMemberResponse(targetB)).willReturn(expectedResponses.get(1));

		// when
		List<MemberResponse> followingMemberResponses = memberService.findAllFollowing(member.getId());

		// then
		Assertions.assertThat(followingMemberResponses.size()).isEqualTo(followings.size());

		AtomicInteger index = new AtomicInteger();
		followingMemberResponses.forEach(response -> {
			MatcherAssert.assertThat(
				expectedResponses.get(index.getAndIncrement()),
				Matchers.samePropertyValuesAs(response)
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