package com.kdt.instakyuram.member.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.kdt.instakyuram.common.PageDto;
import com.kdt.instakyuram.member.domain.Member;

@ExtendWith(MockitoExtension.class)
class MemberConverterTest {

	@InjectMocks
	private MemberConverter converter;

	@DisplayName("Page<Member> -> PageResponse")
	@Test
	void testToPageResponse() {
		//given
		int requestPage = 2;
		int requestSize = 5;

		PageDto.Request request = new PageDto.Request(requestPage, requestSize);
		Pageable pageRequest = new PageDto.Request(requestPage, requestSize).getPageable(Sort.by("id"));
		List<Member> members = getMembers();
		PageImpl<Member> pagingMembers = new PageImpl<>(members, pageRequest, members.size());
		PageDto.Response<MemberResponse.ViewResponse, Member> expectedResponses = new PageDto.Response<>(
			pagingMembers,
			member -> new MemberResponse.ViewResponse(member.getId(), member.getUsername(), member.getName())
		);
		List<MemberResponse.ViewResponse> expectedContents = expectedResponses.getResponses();

		//when
		PageDto.Response<MemberResponse.ViewResponse, Member> responses = converter.toPageResponse(pagingMembers);
		AtomicInteger index = new AtomicInteger();

		//then
		assertThat(responses.getResponses().size()).isEqualTo(expectedContents.size());
		responses.getResponses().forEach(response -> {
				MatcherAssert.assertThat(
					response,
					Matchers.samePropertyValuesAs(expectedContents.get(index.getAndIncrement()))
				);
			}
		);
	}

	private List<Member> getMembers() {

		List<Member> members = new ArrayList<>();

		String name = "programmers";
		String password = "devCourse2!";
		String phoneNumber = "01012345678";
		String emailPostfix = "@programmers.co.kr";

		IntStream.rangeClosed(1, 3).forEach(
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