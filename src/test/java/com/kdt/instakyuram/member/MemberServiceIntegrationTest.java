package com.kdt.instakyuram.member;

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

import com.kdt.instakyuram.exception.NotFoundException;
import com.kdt.instakyuram.follow.domain.Follow;
import com.kdt.instakyuram.member.domain.Member;
import com.kdt.instakyuram.member.domain.MemberRepository;
import com.kdt.instakyuram.member.dto.MemberConverter;
import com.kdt.instakyuram.member.dto.MemberRequest;
import com.kdt.instakyuram.member.dto.MemberResponse;
import com.kdt.instakyuram.member.service.MemberGiver;
import com.kdt.instakyuram.member.service.MemberService;

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
		MemberRequest.SignupRequest signupRequest = new MemberRequest.SignupRequest("pjh123", password, "홍길동",
			"user123@gmail.com", "01012345678");
		MemberResponse.SignupResponse signupResponse = memberGiver.signup(signupRequest);

		//when
		MemberRequest.SigninRequest signinRequest = new MemberRequest.SigninRequest(signupRequest.username(),
			signupRequest.password());
		MemberResponse.SigninResponse signinResponse = memberGiver.signin(signinRequest.username(),
			signinRequest.password());

		//then
		assertThat(signinResponse.username()).isEqualTo(signinRequest.username());
		assertThat(signinResponse.id()).isEqualTo(signupResponse.id());
		assertThat(signinResponse.accessToken()).isNotNull();
		assertThat(signinResponse.refreshToken()).isNotNull();
	}

	@Test
	@DisplayName("Sign in 비밀번호 불일치 테스트")
	void testSigninWithNotMatchingPassword() {
		//given
		MemberRequest.SignupRequest signupRequest = new MemberRequest.SignupRequest("pjh123", password, "홍길동",
			"user123@gmail.com", "01012345678");
		MemberResponse.SignupResponse signupResponse = memberGiver.signup(signupRequest);
		String notMatchingPassword = "876543210";
		MemberRequest.SigninRequest signinRequest = new MemberRequest.SigninRequest(signupRequest.username(),
			notMatchingPassword);

		//when, then
		assertThatThrownBy(() -> memberGiver.signin(signinRequest.username(), signinRequest.password())).isInstanceOf(
			NotFoundException.class);
	}

	@Test
	@DisplayName("없는 Username으로 Sign in 테스트")
	void testSigninWithNotExistUsernmae() {
		//given
		String notExistUsername = "NOTEXISTUSERNAME";
		MemberRequest.SigninRequest signinRequest = new MemberRequest.SigninRequest(notExistUsername, password);

		//when, then
		assertThatThrownBy(() -> memberGiver.signin(signinRequest.username(), signinRequest.password())).isInstanceOf(
			NotFoundException.class);
	}

	@Test
	@DisplayName("sign up 성공 테스트")
	void testSignupSuccess() {
		//given
		MemberRequest.SignupRequest signupRequest = new MemberRequest.SignupRequest("pjh123", password, "홍길동",
			"user123@gmail.com", "01012345678");

		//when
		MemberResponse.SignupResponse signupResponse = memberGiver.signup(signupRequest);

		//then
		assertThat(signupResponse.id()).isNotNull();
		assertThat(signupResponse.username()).isEqualTo(signupRequest.username());
	}

	@Test
	@DisplayName("중복된 아이디로 가입시 실패 테스트")
	void testSignupWithDuplicatedUsername() {
		//given
		MemberRequest.SignupRequest signupRequestA = new MemberRequest.SignupRequest("pjh123", password, "홍길동",
			"user123@gmail.com", "01012345678");
		MemberRequest.SignupRequest signupRequestB = new MemberRequest.SignupRequest(signupRequestA.username(),
			password, "홍길동",
			"user456@gmail.com", "01012345678");
		memberGiver.signup(signupRequestA);

		//when, then
		assertThatThrownBy(() -> memberGiver.signup(signupRequestB)).isInstanceOf(
			DataIntegrityViolationException.class);
	}

	@Test
	@DisplayName("중복된 이메일로 가입시 실패 테스트")
	void testSignupWithDuplicatedEmail() {
		//given
		MemberRequest.SignupRequest signupRequestA = new MemberRequest.SignupRequest("pjh123", password, "홍길동",
			"user123@gmail.com", "01012345678");
		MemberRequest.SignupRequest signupRequestB = new MemberRequest.SignupRequest("pjh123", password, "홍길동",
			signupRequestA.email(), "01012345678");
		memberGiver.signup(signupRequestA);

		//when, then
		assertThatThrownBy(() -> memberGiver.signup(signupRequestB)).isInstanceOf(
			DataIntegrityViolationException.class);
	}

	@Test
	@DisplayName("이메일정보 null로 가입시 실패 테스트")
	void testSignupWithNullEmail() {
		//given
		MemberRequest.SignupRequest signupRequest = new MemberRequest.SignupRequest("pjh123", password, "홍길동",
			null, "01012345678");

		//when, then
		assertThatThrownBy(() -> memberGiver.signup(signupRequest)).isInstanceOf(ConstraintViolationException.class);
	}

	@Test
	@DisplayName("Username null로 가입시 실패 테스트")
	void testSignupWithNullUsername() {
		//given
		MemberRequest.SignupRequest signupRequest = new MemberRequest.SignupRequest(null, password, "홍길동",
			"user123@gmail.com", "01012345678");

		//when, then
		assertThatThrownBy(() -> memberGiver.signup(signupRequest)).isInstanceOf(ConstraintViolationException.class);
	}

	@Test
	@DisplayName("name null로 가입시 실패 테스트")
	void testSignupWithNullName() {
		//given
		MemberRequest.SignupRequest signupRequest = new MemberRequest.SignupRequest("pjh123", password, null,
			"user123@gmail.com", "01012345678");

		//when, then
		assertThatThrownBy(() -> memberGiver.signup(signupRequest)).isInstanceOf(ConstraintViolationException.class);
	}

	@Test
	@DisplayName("phoneNumber null로 가입시 실패 테스트")
	void testSignupWithNullPhoneNumber() {
		//given
		MemberRequest.SignupRequest signupRequest = new MemberRequest.SignupRequest("pjh123", password, "홍길동",
			null, "01012345678");

		//when, then
		assertThatThrownBy(() -> memberGiver.signup(signupRequest)).isInstanceOf(ConstraintViolationException.class);
	}

	@Test
	@DisplayName("id로 멤버 단건 조회 성공 테스트")
	void testFindById() {
		//given
		MemberRequest.SignupRequest signupRequest = new MemberRequest.SignupRequest("pjh123", password, "홍길동",
			"user123@gmail.com", "01012345678");
		MemberResponse.SignupResponse signupResponse = memberGiver.signup(signupRequest);

		//when
		MemberResponse foundMember = memberGiver.findById(signupResponse.id());

		//then
		assertThat(foundMember.id()).isEqualTo(signupResponse.id());
		assertThat(foundMember.username()).isEqualTo(signupResponse.username());
		assertThat(foundMember.name()).isEqualTo(signupRequest.name());
		assertThat(foundMember.email()).isEqualTo(signupRequest.email());
		assertThat(foundMember.phoneNumber()).isEqualTo(signupRequest.phoneNumber());
	}

	@Test
	@DisplayName("없는 id로 멤버 단건 조회 테스트")
	void testFindByNotExistId() {
		//given
		Long notExistId = -987654321L;

		//when, then
		assertThatThrownBy(() -> memberGiver.findById(notExistId)).isInstanceOf(NotFoundException.class);
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
