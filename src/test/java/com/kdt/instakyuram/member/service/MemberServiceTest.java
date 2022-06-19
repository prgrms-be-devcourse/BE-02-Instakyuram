package com.kdt.instakyuram.member.service;

import static org.mockito.BDDMockito.given;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
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