package com.kdt.instakyuram.member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.kdt.instakyuram.common.PageDto;
import com.kdt.instakyuram.exception.NotFoundException;
import com.kdt.instakyuram.follow.service.FollowService;
import com.kdt.instakyuram.member.domain.Member;
import com.kdt.instakyuram.member.domain.MemberRepository;
import com.kdt.instakyuram.member.dto.MemberConverter;
import com.kdt.instakyuram.member.dto.MemberRequest;
import com.kdt.instakyuram.member.dto.MemberResponse;
import com.kdt.instakyuram.security.Role;
import com.kdt.instakyuram.security.jwt.Jwt;
import com.kdt.instakyuram.token.service.TokenService;

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

	@Mock
	PasswordEncoder passwordEncoder;

	@Mock
	TokenService tokenService;

	@Mock
	Jwt jwt;

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
		PageDto.Response<MemberResponse.MemberListViewResponse, Member> pageMemberResponses = memberService.findAll(
			pageRequest);

		//then
		verify(memberRepository, times(1)).findAll(pageRequest);
		verify(memberConverter, times(1)).toPageResponse(pagingMembers);

		assertThat(pageMemberResponses.getPage()).isEqualTo(requestPage);
		assertThat(pageMemberResponses.hasPrevious()).isFalse();
		assertThat(pageMemberResponses.hasNext()).isFalse();
		assertThat(pageMemberResponses.getPageNumbers().size()).isEqualTo(expectedPageNumbers.size());
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
		assertThatThrownBy(() -> {
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
		assertThat(followingMemberResponses.size()).isEqualTo(followings.size());

		AtomicInteger index = new AtomicInteger();
		followingMemberResponses.forEach(response -> {
			MatcherAssert.assertThat(
				expectedResponses.get(index.getAndIncrement()),
				Matchers.samePropertyValuesAs(response)
			);
		});
	}

	@Test
	@DisplayName("나 포함, 팔로잉한 사용자 목록 조회")
	void testFindAllFollowingIncludeMe() {
		List<Member> members = getDemoMembers();

		Member member = members.get(0);
		Member targetA = members.get(1);
		Member targetB = members.get(2);

		List<Member> followings = List.of(targetA, targetB);
		List<Member> followingsIncludeMe = List.of(targetA, targetB, member);
		List<Long> followingIds = followings.stream().map(Member::getId).toList();

		List<MemberResponse> expectedResponses = new ArrayList<>(followings.stream()
			.map(following -> MemberResponse.builder()
				.id(following.getId())
				.email(following.getEmail())
				.username(following.getUsername())
				.name(following.getName())
				.phoneNumber(following.getPhoneNumber())
				.introduction("")
				.build())
			.toList());

		expectedResponses.add(MemberResponse.builder().
			id(member.getId())
			.email(member.getEmail())
			.username(member.getEmail())
			.name(member.getName())
			.phoneNumber(member.getPhoneNumber())
			.introduction("")
			.build());

		ArrayList<Long> followingIdsIncludeMe = new ArrayList<>(followingIds);
		followingIdsIncludeMe.add(member.getId());

		// given
		given(followService.findByFollowingIds(member.getId())).willReturn(followingIds);
		given(memberRepository.findAllIdsInOrById(followingIds, member.getId())).willReturn(followingsIncludeMe);
		given(memberConverter.toMemberResponse(targetA)).willReturn(expectedResponses.get(0));
		given(memberConverter.toMemberResponse(targetB)).willReturn(expectedResponses.get(1));
		given(memberConverter.toMemberResponse(member)).willReturn(expectedResponses.get(2));

		// when
		List<MemberResponse> followingMemberResponses = memberService.findAllFollowingIncludeMe(member.getId());

		// then
		assertThat(followingMemberResponses).hasSameSizeAs(followingIdsIncludeMe);

		AtomicInteger index = new AtomicInteger();
		followingMemberResponses.forEach(response -> {
			MatcherAssert.assertThat(
				expectedResponses.get(index.getAndIncrement()),
				Matchers.samePropertyValuesAs(response)
			);
		});
	}

	@Test
	@DisplayName("Sign up 테스트")
	void testSignup() {
		//given
		MemberRequest.SignupRequest request = new MemberRequest.SignupRequest("pjh123", "123456789", "홍길동",
			"user123@gmail.com", "01012345678");
		Member member = new Member(
			1L,
			request.username(),
			request.name(),
			request.password(),
			request.phoneNumber(),
			request.email(),
			""
		);
		MemberResponse.SignupResponse response = new MemberResponse.SignupResponse(member.getId(), request.username());

		given(memberRepository.save(any(Member.class))).willReturn(member);
		//when
		MemberResponse.SignupResponse signupResponse = memberService.signup(request);

		//then
		verify(memberRepository, times(1)).save(any(Member.class));

		assertThat(signupResponse.id()).isEqualTo(member.getId());
		assertThat(signupResponse.username()).isEqualTo(request.username());
	}

	@Test
	@DisplayName("Sign in 비밀번호 불일치 테스트")
	void testSigninWithNotMatchingPassword() {
		//given
		MemberRequest.SignupRequest request = new MemberRequest.SignupRequest("pjh123", "123456789", "홍길동",
			"user123@gmail.com", "01012345678");
		Member member = new Member(
			1L,
			request.username(),
			request.name(),
			request.password(),
			request.phoneNumber(),
			request.email(),
			""
		);
		String notMatchingPassword = "876543210";
		MemberRequest.SigninRequest signinRequest = new MemberRequest.SigninRequest(member.getUsername(),
			notMatchingPassword);

		given(memberRepository.findByUsername(signinRequest.username())).willReturn(Optional.of(member));
		given(passwordEncoder.matches(signinRequest.password(), member.getPassword())).willReturn(false);

		//when, then
		assertThatThrownBy(() -> memberService.signin(signinRequest.username(), signinRequest.password())).isInstanceOf(
			NotFoundException.class);

		verify(memberRepository, times(1)).findByUsername(signinRequest.username());
		verify(passwordEncoder, times(1)).matches(signinRequest.password(), member.getPassword());
	}

	@Test
	@DisplayName("Sign in 테스트")
	void testSignin() {
		//given
		Member member = new Member(
			1L,
			"pjh123",
			"홍길동",
			"encodedPassword",
			"user123@gmail.com",
			"01012345678",
			"");
		String accessToken = "accessToken";
		String refreshToken = "refreshToken";
		String[] roles = {String.valueOf(Role.MEMBER)};
		MemberRequest.SigninRequest request = new MemberRequest.SigninRequest(
			member.getUsername(),
			"123456789"
		);
		MemberResponse.SigninResponse response = new MemberResponse.SigninResponse(
			member.getId(),
			member.getUsername(),
			accessToken,
			refreshToken,
			roles
		);

		given(passwordEncoder.matches(request.password(), member.getPassword())).willReturn(true);
		given(memberRepository.findByUsername(request.username())).willReturn(Optional.of(member));
		given(jwt.generateAccessToken(any(Long.class), any(String[].class))).willReturn(accessToken);
		given(jwt.generateRefreshToken()).willReturn(refreshToken);
		given(tokenService.save(refreshToken, member.getId())).willReturn(refreshToken);

		//when
		MemberResponse.SigninResponse signinResponse = memberService.signin(request.username(), request.password());

		//then
		verify(passwordEncoder, times(1)).matches(request.password(), member.getPassword());
		verify(memberRepository, times(1)).findByUsername(request.username());
		verify(jwt, times(1)).generateAccessToken(any(Long.class), any(String[].class));
		verify(jwt, times(1)).generateRefreshToken();
		verify(tokenService, times(1)).save(refreshToken, member.getId());

		assertThat(signinResponse.accessToken()).isEqualTo(accessToken);
		assertThat(signinResponse.refreshToken()).isEqualTo(refreshToken);
		assertThat(signinResponse.id()).isEqualTo(member.getId());
		assertThat(signinResponse.username()).isEqualTo(member.getUsername());
	}

	@Test
	@DisplayName("id로 멤버 단건 조회 성공 테스트")
	void testFindById() {
		//given
		Member member = new Member(1L,
			"pjh123",
			"홍길동",
			"encodedPassword",
			"user123@gmail.com",
			"01012345678",
			"");

		given(memberRepository.findById(member.getId())).willReturn(Optional.of(member));

		//when
		MemberResponse foundMember = memberService.findById(member.getId());

		//then
		verify(memberRepository, times(1)).findById(member.getId());

		assertThat(foundMember.id()).isEqualTo(member.getId());
		assertThat(foundMember.username()).isEqualTo(member.getUsername());
		assertThat(foundMember.name()).isEqualTo(member.getName());
		assertThat(foundMember.email()).isEqualTo(member.getEmail());
		assertThat(foundMember.phoneNumber()).isEqualTo(member.getPhoneNumber());
	}

	@Test
	@DisplayName("없는 id로 멤버 단건 조회 테스트")
	void testFindByNotExistId() {
		//given
		Long notExistId = -987654321L;
		given(memberRepository.findById(notExistId)).willReturn(Optional.empty());

		//when
		assertThatThrownBy(() -> memberService.findById(notExistId)).isInstanceOf(NotFoundException.class);

		verify(memberRepository, times(1)).findById(notExistId);
	}

	@Test
	@DisplayName("나를 따르는 사람의 수 구하기 =: follower 수")
	void testCountMyFollower() {
		//given
		Long expectedMyFollower = 10L;
		Long myId = 1L;

		given(followService.countMyFollower(myId)).willReturn(expectedMyFollower);

		//when
		Long myFollower = memberService.countMyFollower(myId);

		//then
		Assertions.assertNotNull(myFollower);
		assertThat(myFollower).isEqualTo(expectedMyFollower);

		verify(followService, times(1)).countMyFollower(myId);
	}

	@Test
	@DisplayName("내가 따르는 사람의 수 구하기 =: following 수")
	void testCountMyFollowing() {
		//given
		Long expectedMyFollower = 10L;
		Long myId = 1L;

		given(followService.countMyFollowing(myId)).willReturn(expectedMyFollower);

		//when
		Long myFollowing = memberService.countMyFollowing(myId);

		//then
		Assertions.assertNotNull(myFollowing);
		assertThat(myFollowing).isEqualTo(expectedMyFollower);

		verify(followService, times(1)).countMyFollowing(myId);
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