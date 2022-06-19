package com.kdt.instakyuram.member.dto;

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
import org.mockito.junit.jupiter.MockitoExtension;

import com.kdt.instakyuram.member.domain.Member;

@ExtendWith(MockitoExtension.class)
class MemberConverterTest {

	@InjectMocks
	private MemberConverter memberConverter;

	@Test
	@DisplayName("Member entity -> MemberResponse 객체로 변환")
	void toMemberResponse() {
		//given
		List<Member> members = getDemoMembers();

		List<MemberResponse> expectedResponses = members.stream().map(member -> MemberResponse.builder()
				.id(member.getId())
				.email(member.getEmail())
				.username(member.getUsername())
				.name(member.getName())
				.phoneNumber(member.getPhoneNumber())
				.build())
			.toList();

		//when
		List<MemberResponse> memberResponses = members.stream()
			.map(memberConverter::toMemberResponse)
			.toList();

		//when
		Assertions.assertThat(memberResponses.size()).isEqualTo(members.size());

		AtomicInteger index = new AtomicInteger();

		memberResponses.forEach(response -> {
			MatcherAssert.assertThat(response,
				Matchers.samePropertyValuesAs(expectedResponses.get(index.getAndIncrement())));
		});

	}

	private List<Member> getDemoMembers() {

		List<Member> members = new ArrayList<>();

		String name = "programmers";
		String password = "password";
		String phoneNumber = "01012345678";
		String emailPostfix = "@programmers.co.kr";

		LongStream.range(1, 5).forEach(
			number -> members.add(Member.builder()
				.id(number)
				.email((name + number) + emailPostfix)
				.password(password)
				.username(name + number)
				.phoneNumber(phoneNumber)
				.name(name)
				.build()
			)
		);

		return members;
	}
}