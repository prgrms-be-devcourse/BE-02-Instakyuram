package com.kdt.instakyuram.user.member.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import javax.validation.ConstraintViolationException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.kdt.instakyuram.configure.WebSecurityTestConfigure;

@DataJpaTest
@Import(WebSecurityTestConfigure.class)
public class MemberValidationTest {

	@Autowired
	TestEntityManager entityManager;

	@Autowired
	MemberRepository memberRepository;

	@Autowired
	PasswordEncoder passwordEncoder;

	@Test
	@DisplayName("Member 추가 성공 테스트")
	void testSaveSuccess() {
		//given
		String encodedPassword = passwordEncoder.encode("@Password12");
		Member member = new Member(
			"pjh123",
			encodedPassword,
			"홍길동",
			"01012345678",
			"user@gmail.com",
			""
		);

		//when
		Member savedMember = memberRepository.save(member);

		//then
		assertThat(savedMember.getId()).isNotNull();
		assertThat(savedMember.getUsername()).isEqualTo(member.getUsername());
		assertThat(savedMember.getPassword()).isEqualTo(member.getPassword());
		assertThat(savedMember.getName()).isEqualTo(member.getName());
		assertThat(savedMember.getEmail()).isEqualTo(member.getEmail());
		assertThat(savedMember.getPhoneNumber()).isEqualTo(member.getPhoneNumber());

	}

	@Test
	@DisplayName("중복된 아이디로 가입시 실패 테스트")
	void testSaveWithDuplicatedUsername() {
		//given
		String encodedPassword = passwordEncoder.encode("@Password12");
		Member memberA = new Member(
			"user123",
			encodedPassword,
			"홍길동",
			"01012345678",
			"userA@gmail.com",
			"");
		Member memberB = new Member(
			memberA.getUsername(),
			encodedPassword,
			"김길동",
			"01012345678",
			"userB@gmail.com",
			"");
		memberRepository.save(memberA);

		//when, then
		assertThatThrownBy(() -> memberRepository.save(memberB)).isInstanceOf(DataIntegrityViolationException.class);
	}

	@Test
	@DisplayName("중복된 이메일로 가입시 실패 테스트")
	void testSaveWithDuplicatedEmail() {
		//given
		String encodedPassword = passwordEncoder.encode("@Password12");
		Member memberA = new Member(
			"user123",
			encodedPassword,
			"홍길동",
			"01012345678",
			"user@gmail.com",
			"");
		Member memberB = new Member(
			"user124",
			encodedPassword,
			"김길동",
			"01012345678",
			memberA.getEmail(),
			"");
		memberRepository.save(memberA);

		//when, then
		assertThatThrownBy(() -> memberRepository.save(memberB)).isInstanceOf(DataIntegrityViolationException.class);
	}

	@Test
	@DisplayName("이메일정보 null로 가입시 실패 테스트")
	void testSaveWithNullEmail() {
		//given
		String encodedPassword = passwordEncoder.encode("@Password12");
		Member member = new Member(
			"user123",
			encodedPassword,
			"홍길동",
			"01012345678",
			null,
			"");

		//when, then
		assertThatThrownBy(() -> memberRepository.save(member)).isInstanceOf(ConstraintViolationException.class);
	}

	@Test
	@DisplayName("Username null로 가입시 실패 테스트")
	void testSaveWithNullUsername() {
		//given
		String encodedPassword = passwordEncoder.encode("@Password12");
		Member member = new Member(
			null,
			encodedPassword,
			"홍길동",
			"01012345678",
			"user123@gmail.com",
			"");

		//when, then
		assertThatThrownBy(() -> memberRepository.save(member)).isInstanceOf(ConstraintViolationException.class);
	}

	@Test
	@DisplayName("Username blank로 가입시 실패 테스트")
	void testSaveWithBlankUsername() {
		//given
		String encodedPassword = passwordEncoder.encode("@Password12");
		Member member = new Member(
			"",
			encodedPassword,
			"홍길동",
			"01012345678",
			"user123@gmail.com",
			"");

		//when, then
		assertThatThrownBy(() -> memberRepository.save(member)).isInstanceOf(ConstraintViolationException.class);
	}

	@Test
	@DisplayName("대문자 포함 Username 가입시 실패 테스트")
	void testSaveWithIncludeCapitalUsername() {
		//given
		String encodedPassword = passwordEncoder.encode("@Password12");
		Member member = new Member(
			"Pjh123",
			encodedPassword,
			"홍길동",
			"01012345678",
			"user123@gmail.com",
			"");

		//when, then
		assertThatThrownBy(() -> memberRepository.save(member)).isInstanceOf(ConstraintViolationException.class);
	}

	@Test
	@DisplayName("name null로 가입시 실패 테스트")
	void testSaveWithNullName() {
		//given
		String encodedPassword = passwordEncoder.encode("@Password12");
		Member member = new Member(
			"user123",
			encodedPassword,
			null,
			"01012345678",
			"user123@gmail.com",
			"");

		//when, then
		assertThatThrownBy(() -> memberRepository.save(member)).isInstanceOf(ConstraintViolationException.class);
	}

	@Test
	@DisplayName("name blank로 가입시 실패 테스트")
	void testSaveWithBlankName() {
		//given
		String encodedPassword = passwordEncoder.encode("@Password12");
		Member member = new Member(
			"user123",
			encodedPassword,
			"",
			"01012345678",
			"user123@gmail.com",
			"");

		//when, then
		assertThatThrownBy(() -> memberRepository.save(member)).isInstanceOf(ConstraintViolationException.class);
	}

	@Test
	@DisplayName("phoneNumber null로 가입시 실패 테스트")
	void testSaveWithNullPhoneNumber() {
		//given
		String encodedPassword = passwordEncoder.encode("@Password12");
		Member member = new Member(
			"user123",
			encodedPassword,
			"홍길동",
			null,
			"user123@gmail.com",
			"");

		//when, then
		assertThatThrownBy(() -> memberRepository.save(member)).isInstanceOf(ConstraintViolationException.class);
	}

	@Test
	@DisplayName("test")
	void testToo() {
		//given
		order asc = order.valueOf("asc");
		//when

		//then
	}
}

enum order {
	ASC("asc"), DESC("desc");

	private final String lowerCase;

	order(String lowerCase) {
		this.lowerCase = lowerCase;
	}


}
