package com.kdt.instakyuram.member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.kdt.instakyuram.exception.NotFoundException;
import com.kdt.instakyuram.member.domain.Member;
import com.kdt.instakyuram.member.domain.MemberRepository;
import com.kdt.instakyuram.member.dto.MemberRequest;
import com.kdt.instakyuram.member.dto.MemberResponse;
import com.kdt.instakyuram.security.jwt.Jwt;
import com.kdt.instakyuram.token.service.TokenService;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

	@InjectMocks
	MemberService memberService;

	@Mock
	MemberRepository memberRepository;

	@Mock
	PasswordEncoder passwordEncoder;

	@Mock
	TokenService tokenService;

	@Mock
	Jwt jwt;

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
			request.email()
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
			request.email()
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
		Member member = new Member(1L, "pjh123", "홍길동", "encodedPassword",
			"user123@gmail.com", "01012345678");
		String accessToken = "accessToken";
		String refreshToken = "refreshToken";
		MemberRequest.SigninRequest request = new MemberRequest.SigninRequest(
			member.getUsername(),
			"123456789"
		);
		MemberResponse.SigninResponse response = new MemberResponse.SigninResponse(
			member.getId(),
			member.getUsername(),
			accessToken,
			refreshToken
		);

		given(passwordEncoder.matches(request.password(), member.getPassword())).willReturn(true);
		given(memberRepository.findByUsername(request.username())).willReturn(Optional.of(member));
		given(jwt.generateAccessToken(any(Jwt.Claims.class))).willReturn(accessToken);
		given(jwt.generateRefreshToken()).willReturn(refreshToken);
		given(tokenService.save(refreshToken, member.getId())).willReturn(refreshToken);

		//when
		MemberResponse.SigninResponse signinResponse = memberService.signin(request.username(), request.password());

		//then
		verify(passwordEncoder, times(1)).matches(request.password(), member.getPassword());
		verify(memberRepository, times(1)).findByUsername(request.username());
		verify(jwt, times(1)).generateAccessToken(any(Jwt.Claims.class));
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
		Member member = new Member(1L, "pjh123", "홍길동", "encodedPassword",
			"user123@gmail.com", "01012345678");

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
}