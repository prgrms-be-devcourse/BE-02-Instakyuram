package com.kdt.instakyuram.user.member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.LongStream;

import javax.persistence.EntityManager;
import javax.validation.ConstraintViolationException;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import com.kdt.instakyuram.exception.BusinessException;
import com.kdt.instakyuram.exception.EntityNotFoundException;
import com.kdt.instakyuram.user.follow.domain.Follow;
import com.kdt.instakyuram.user.member.domain.Member;
import com.kdt.instakyuram.user.member.domain.MemberRepository;
import com.kdt.instakyuram.user.member.dto.MemberConverter;
import com.kdt.instakyuram.user.member.dto.MemberRequest;
import com.kdt.instakyuram.user.member.dto.MemberResponse;

@SpringBootTest
@Transactional
class MemberServiceIntegrationTest {

	@Autowired
	private EntityManager entityManager;

	@Autowired
	private MemberGiver memberGiver;

	@Autowired
	private MemberConverter memberConverter;

	@Autowired
	private MemberService memberService;

	@Autowired
	private MemberRepository memberRepository;

	String password = "@Test12345678";

	@BeforeEach
	public void eachInit() {
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
		List<MemberResponse> followings = memberGiver.findAllFollowing(member.getId());

		//then
		assertThat(followings.size()).isEqualTo(expectedFollowings.size());

		AtomicInteger index = new AtomicInteger();

		followings.forEach(following -> {
			MatcherAssert.assertThat(
				following,
				Matchers.samePropertyValuesAs(expectedFollowings.get(index.getAndIncrement()))
			);
		});
	}

	@Test
	@Transactional
	@DisplayName("나 포함 팔로잉 목록 멤버를 조회")
	void testFindAllFollowingIncludeMe() {
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

		List<MemberResponse> expectedFollowings = List.of(targetA, targetB,member).stream()
			.map(memberConverter::toMemberResponse)
			.toList();

		//when
		List<MemberResponse> followings = memberGiver.findAllFollowingIncludeMe(member.getId());

		//then
		assertThat(followings.size()).isEqualTo(expectedFollowings.size());

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

				memberRepository.save(member);

				members.add(member);
			}
		);

		return members;
	}

	@Test
	@DisplayName("sign in 성공 테스트")
	void testSigninSuccess() {
		//given
		MemberRequest.SignUpRequest signUpRequest = new MemberRequest.SignUpRequest("pjh123", password, "홍길동",
			"user123@gmail.com", "01012345678");
		MemberResponse.SignUpResponse signUpResponse = memberGiver.signUp(signUpRequest);

		//when
		MemberRequest.SignInRequest signInRequest = new MemberRequest.SignInRequest(signUpRequest.username(),
			signUpRequest.password());
		MemberResponse.SignInResponse signInResponse = memberGiver.signIn(signInRequest.username(),
			signInRequest.password());

		//then
		assertThat(signInResponse.username()).isEqualTo(signInRequest.username());
		assertThat(signInResponse.id()).isEqualTo(signUpResponse.id());
		assertThat(signInResponse.accessToken()).isNotNull();
		assertThat(signInResponse.refreshToken()).isNotNull();
	}

	@Test
	@DisplayName("Sign in 비밀번호 불일치 테스트")
	void testSignInWithNotMatchingPassword() {
		//given
		MemberRequest.SignUpRequest signUpRequest = new MemberRequest.SignUpRequest("pjh123", password, "홍길동",
			"user123@gmail.com", "01012345678");
		MemberResponse.SignUpResponse signUpResponse = memberGiver.signUp(signUpRequest);
		String notMatchingPassword = "876543210";
		MemberRequest.SignInRequest signInRequest = new MemberRequest.SignInRequest(signUpRequest.username(),
			notMatchingPassword);

		//when, then
		assertThatThrownBy(() -> memberGiver.signIn(signInRequest.username(), signInRequest.password())).isInstanceOf(
			BusinessException.class);
	}

	@Test
	@DisplayName("없는 Username으로 Sign in 테스트")
	void testSignInWithNotExistUsername() {
		//given
		String notExistUsername = "NOTEXISTUSERNAME";
		MemberRequest.SignInRequest signInRequest = new MemberRequest.SignInRequest(notExistUsername, password);

		//when, then
		assertThatThrownBy(() -> memberGiver.signIn(signInRequest.username(), signInRequest.password())).isInstanceOf(
			BusinessException.class);
	}

	@Test
	@DisplayName("sign up 성공 테스트")
	void testSignUpSuccess() {
		//given
		MemberRequest.SignUpRequest signUpRequest = new MemberRequest.SignUpRequest("pjh123", password, "홍길동",
			"user123@gmail.com", "01012345678");

		//when
		MemberResponse.SignUpResponse signUpResponse = memberGiver.signUp(signUpRequest);

		//then
		assertThat(signUpResponse.id()).isNotNull();
		assertThat(signUpResponse.username()).isEqualTo(signUpRequest.username());
	}

	@Test
	@DisplayName("중복된 아이디로 가입시 실패 테스트")
	void testSignUpWithDuplicatedUsername() {
		//given
		MemberRequest.SignUpRequest signUpRequestA = new MemberRequest.SignUpRequest("pjh123", password, "홍길동",
			"user123@gmail.com", "01012345678");
		MemberRequest.SignUpRequest signUpRequestB = new MemberRequest.SignUpRequest(signUpRequestA.username(),
			password, "홍길동",
			"user456@gmail.com", "01012345678");
		memberGiver.signUp(signUpRequestA);

		//when, then
		assertThatThrownBy(() -> memberGiver.signUp(signUpRequestB)).isInstanceOf(
			DataIntegrityViolationException.class);
	}

	@Test
	@DisplayName("중복된 이메일로 가입시 실패 테스트")
	void testSignUpWithDuplicatedEmail() {
		//given
		MemberRequest.SignUpRequest signUpRequestA = new MemberRequest.SignUpRequest("pjh123", password, "홍길동",
			"user123@gmail.com", "01012345678");
		MemberRequest.SignUpRequest signUpRequestB = new MemberRequest.SignUpRequest("pjh123", password, "홍길동",
			signUpRequestA.email(), "01012345678");
		memberGiver.signUp(signUpRequestA);

		//when, then
		assertThatThrownBy(() -> memberGiver.signUp(signUpRequestB)).isInstanceOf(
			DataIntegrityViolationException.class);
	}

	@Test
	@DisplayName("이메일정보 null로 가입시 실패 테스트")
	void testSignUpWithNullEmail() {
		//given
		MemberRequest.SignUpRequest signUpRequest = new MemberRequest.SignUpRequest("pjh123", password, "홍길동",
			null, "01012345678");

		//when, then
		assertThatThrownBy(() -> memberGiver.signUp(signUpRequest)).isInstanceOf(ConstraintViolationException.class);
	}

	@Test
	@DisplayName("Username null로 가입시 실패 테스트")
	void testSignUpWithNullUsername() {
		//given
		MemberRequest.SignUpRequest signUpRequest = new MemberRequest.SignUpRequest(null, password, "홍길동",
			"user123@gmail.com", "01012345678");

		//when, then
		assertThatThrownBy(() -> memberGiver.signUp(signUpRequest)).isInstanceOf(ConstraintViolationException.class);
	}

	@Test
	@DisplayName("name null로 가입시 실패 테스트")
	void testSignUpWithNullName() {
		//given
		MemberRequest.SignUpRequest signUpRequest = new MemberRequest.SignUpRequest("pjh123", password, null,
			"user123@gmail.com", "01012345678");

		//when, then
		assertThatThrownBy(() -> memberGiver.signUp(signUpRequest)).isInstanceOf(ConstraintViolationException.class);
	}

	@Test
	@DisplayName("phoneNumber null로 가입시 실패 테스트")
	void testSignUpWithNullPhoneNumber() {
		//given
		MemberRequest.SignUpRequest signUpRequest = new MemberRequest.SignUpRequest("pjh123", password, "홍길동",
			null, "01012345678");

		//when, then
		assertThatThrownBy(() -> memberGiver.signUp(signUpRequest)).isInstanceOf(ConstraintViolationException.class);
	}

	@Test
	@DisplayName("id로 멤버 단건 조회 성공 테스트")
	void testFindById() {
		//given
		MemberRequest.SignUpRequest signUpRequest = new MemberRequest.SignUpRequest("pjh123", password, "홍길동",
			"user123@gmail.com", "01012345678");
		MemberResponse.SignUpResponse signUpResponse = memberGiver.signUp(signUpRequest);

		//when
		MemberResponse foundMember = memberGiver.findById(signUpResponse.id());

		//then
		assertThat(foundMember.id()).isEqualTo(signUpResponse.id());
		assertThat(foundMember.username()).isEqualTo(signUpResponse.username());
		assertThat(foundMember.name()).isEqualTo(signUpRequest.name());
		assertThat(foundMember.email()).isEqualTo(signUpRequest.email());
		assertThat(foundMember.phoneNumber()).isEqualTo(signUpRequest.phoneNumber());
	}

	@Test
	@DisplayName("없는 id로 멤버 단건 조회 테스트")
	void testFindByNotExistId() {
		//given
		Long notExistId = -987654321L;

		//when, then
		assertThatThrownBy(() -> memberGiver.findById(notExistId)).isInstanceOf(EntityNotFoundException.class);
	}

	@Test
	@Transactional
	@DisplayName("나를 따르는 사람의 수 구하기 =: follower 수")
	void testCountMyFollower() {
		//given
		List<Member> members = getDemoMembers();
		Member my = members.get(0);
		Member fromA = members.get(1);
		Member fromB = members.get(2);

		entityManager.persist(Follow.builder()
			.memberId(fromA.getId())
			.targetId(my.getId())
			.build());

		entityManager.persist(Follow.builder()
			.memberId(fromB.getId())
			.targetId(my.getId())
			.build());

		Long expectedMyFollower = (long)(List.of(fromA, fromB).size());

		//when
		Long myFollower = memberService.countMyFollower(my.getId());

		//then
		Assertions.assertNotNull(myFollower);
		assertThat(myFollower).isEqualTo(expectedMyFollower);
	}

	@Test
	@Transactional
	@DisplayName("내가 따르는 사람의 수 구하기 =: following 수")
	void testCountMyFollowing() {
		// given
		List<Member> members = getDemoMembers();
		Member my = members.get(2);
		Member followingA = members.get(1);

		entityManager.persist(
			Follow.builder()
				.memberId(my.getId())
				.targetId(followingA.getId())
				.build()
		);

		Long expectedFollowing = (long)List.of(followingA).size();

		// when
		Long following = memberService.countMyFollowing(my.getId());

		// then
		Assertions.assertNotNull(following);
		assertThat(following).isEqualTo(expectedFollowing);
	}

}
