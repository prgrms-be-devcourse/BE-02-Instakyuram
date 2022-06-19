package com.kdt.instakyuram.member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import javax.validation.ConstraintViolationException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import com.kdt.instakyuram.exception.NotFoundException;
import com.kdt.instakyuram.member.dto.MemberRequest;
import com.kdt.instakyuram.member.dto.MemberResponse;

@SpringBootTest
@Transactional
class MemberServiceIntegrationTest {

	@Autowired
	MemberService memberService;

	String password = "@Test12345678";

	@Test
	@DisplayName("sign in 성공 테스트")
	void testSigninSuccess() {
		//given
		MemberRequest.SignupRequest signupRequest = new MemberRequest.SignupRequest("pjh123", password, "홍길동",
			"user123@gmail.com", "01012345678");
		MemberResponse.SignupResponse signupResponse = memberService.signup(signupRequest);

		//when
		MemberRequest.SigninRequest signinRequest = new MemberRequest.SigninRequest(signupRequest.username(),
			signupRequest.password());
		MemberResponse.SigninResponse signinResponse = memberService.signin(signinRequest.username(),
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
		MemberResponse.SignupResponse signupResponse = memberService.signup(signupRequest);
		String notMatchingPassword = "876543210";
		MemberRequest.SigninRequest signinRequest = new MemberRequest.SigninRequest(signupRequest.username(),
			notMatchingPassword);

		//when, then
		assertThatThrownBy(() -> memberService.signin(signinRequest.username(), signinRequest.password())).isInstanceOf(
			NotFoundException.class);
	}

	@Test
	@DisplayName("없는 Username으로 Sign in 테스트")
	void testSigninWithNotExistUsernmae() {
		//given
		String notExistUsername = "NOTEXISTUSERNAME";
		MemberRequest.SigninRequest signinRequest = new MemberRequest.SigninRequest(notExistUsername, password);

		//when, then
		assertThatThrownBy(() -> memberService.signin(signinRequest.username(), signinRequest.password())).isInstanceOf(
			NotFoundException.class);
	}

	@Test
	@DisplayName("sign up 성공 테스트")
	void testSignupSuccess() {
		//given
		MemberRequest.SignupRequest signupRequest = new MemberRequest.SignupRequest("pjh123", password, "홍길동",
			"user123@gmail.com", "01012345678");

		//when
		MemberResponse.SignupResponse signupResponse = memberService.signup(signupRequest);

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
		memberService.signup(signupRequestA);

		//when, then
		assertThatThrownBy(() -> memberService.signup(signupRequestB)).isInstanceOf(
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
		memberService.signup(signupRequestA);

		//when, then
		assertThatThrownBy(() -> memberService.signup(signupRequestB)).isInstanceOf(
			DataIntegrityViolationException.class);
	}

	@Test
	@DisplayName("이메일정보 null로 가입시 실패 테스트")
	void testSignupWithNullEmail() {
		//given
		MemberRequest.SignupRequest signupRequest = new MemberRequest.SignupRequest("pjh123", password, "홍길동",
			null, "01012345678");

		//when, then
		assertThatThrownBy(() -> memberService.signup(signupRequest)).isInstanceOf(ConstraintViolationException.class);
	}

	@Test
	@DisplayName("Username null로 가입시 실패 테스트")
	void testSignupWithNullUsername() {
		//given
		MemberRequest.SignupRequest signupRequest = new MemberRequest.SignupRequest(null, password, "홍길동",
			"user123@gmail.com", "01012345678");

		//when, then
		assertThatThrownBy(() -> memberService.signup(signupRequest)).isInstanceOf(ConstraintViolationException.class);
	}

	@Test
	@DisplayName("name null로 가입시 실패 테스트")
	void testSignupWithNullName() {
		//given
		MemberRequest.SignupRequest signupRequest = new MemberRequest.SignupRequest("pjh123", password, null,
			"user123@gmail.com", "01012345678");

		//when, then
		assertThatThrownBy(() -> memberService.signup(signupRequest)).isInstanceOf(ConstraintViolationException.class);
	}

	@Test
	@DisplayName("phoneNumber null로 가입시 실패 테스트")
	void testSignupWithNullPhoneNumber() {
		//given
		MemberRequest.SignupRequest signupRequest = new MemberRequest.SignupRequest("pjh123", password, "홍길동",
			null, "01012345678");

		//when, then
		assertThatThrownBy(() -> memberService.signup(signupRequest)).isInstanceOf(ConstraintViolationException.class);
	}

	@Test
	@DisplayName("id로 멤버 단건 조회 성공 테스트")
	void testFindById() {
		//given
		MemberRequest.SignupRequest signupRequest = new MemberRequest.SignupRequest("pjh123", password, "홍길동",
			"user123@gmail.com", "01012345678");
		MemberResponse.SignupResponse signupResponse = memberService.signup(signupRequest);

		//when
		MemberResponse foundMember = memberService.findById(signupResponse.id());

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
		assertThatThrownBy(() -> memberService.findById(notExistId)).isInstanceOf(NotFoundException.class);
	}
}
