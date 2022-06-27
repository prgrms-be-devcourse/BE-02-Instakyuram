/*
package com.kdt.instakyuram.profileimage.domain;

import java.util.Optional;
import java.util.UUID;

import javax.validation.ConstraintViolationException;

import org.assertj.core.api.Assertions;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import com.kdt.instakyuram.configure.TestJpaAuditConfig;
import com.kdt.instakyuram.member.domain.Member;

@Import(TestJpaAuditConfig.class)
@DataJpaTest
public class ProfileImageRepositoryTest {

	@Autowired
	private ProfileImageRepository profileImageRepository;

	@Autowired
	private TestEntityManager testEntityManager;

	private String originalFileName = "test.png";
	private String serverFileName = UUID.randomUUID().toString();
	private String path = "classpath:/image/profile/";
	private Long size = 1234L;

	@Test
	@DisplayName("member 없이 프로필을 저장하려 할때 Validation 오류가 난다.")
	void testSaveNotContainMember() {
		// given
		ProfileImage profileImage = ProfileImage.builder()
			.originalFileName(originalFileName)
			.serverFileName(serverFileName)
			.path(path)
			.size(size)
			.build();

		// when
		// then
		Assertions.assertThatThrownBy(() -> {
			profileImageRepository.save(profileImage);
		}).isInstanceOf(ConstraintViolationException.class);
	}

	@Test
	@DisplayName("원본 파일 이름 없이 프로필을 저장하려 할때 Validation 오류가 난다.")
	void testSaveNotContainOriginalFileName() {
		// given
		ProfileImage profileImage = ProfileImage.builder()
			.member(getDemoMember())
			.serverFileName(serverFileName)
			.path(path)
			.size(size)
			.build();

		// when
		// then
		Assertions.assertThatThrownBy(() -> {
			profileImageRepository.save(profileImage);
		}).isInstanceOf(ConstraintViolationException.class);
	}

	@Test
	@DisplayName("서버 파일 이름 없이 프로필을 저장하려 할때 Validation 오류가 난다.")
	void testSaveNotContainServerFileName() {
		// given
		ProfileImage profileImage = ProfileImage.builder()
			.member(getDemoMember())
			.originalFileName(originalFileName)
			.path(path)
			.size(size)
			.build();

		// when
		// then
		Assertions.assertThatThrownBy(() -> {
			profileImageRepository.save(profileImage);
		}).isInstanceOf(ConstraintViolationException.class);
	}

	@Test
	@DisplayName("사이즈 없이 프로필을 저장하려 할때 Validation 오류가 난다.")
	void testSaveNotContainSize() {
		// given
		ProfileImage profileImage = ProfileImage.builder()
			.member(getDemoMember())
			.originalFileName(originalFileName)
			.serverFileName(serverFileName)
			.path(path)
			.build();

		// when
		// then
		Assertions.assertThatThrownBy(() -> {
			profileImageRepository.save(profileImage);
		}).isInstanceOf(ConstraintViolationException.class);
	}

	@Test
	@DisplayName("사이즈 값에 10485760보다 큰 값을 넣고 프로필을 저장하려 할때 Validation 오류가 난다.")
	void testSaveNegativeValueSize() {
		// given
		Long overSize = 100_000_000L;
		ProfileImage profileImage = ProfileImage.builder()
			.member(getDemoMember())
			.originalFileName(originalFileName)
			.serverFileName(serverFileName)
			.path(path)
			.size(overSize)
			.build();

		// when
		// then
		Assertions.assertThatThrownBy(() -> {
			profileImageRepository.save(profileImage);
		}).isInstanceOf(ConstraintViolationException.class);
	}

	@Test
	@DisplayName("사이즈 값에 음의 값을 넣고 프로필을 저장하려 할때 Validation 오류가 난다.")
	void testSaveOverValueSize() {
		// given
		ProfileImage profileImage = ProfileImage.builder()
			.member(getDemoMember())
			.originalFileName(originalFileName)
			.serverFileName(serverFileName)
			.path(path)
			.size(-123L)
			.build();

		// when
		// then
		Assertions.assertThatThrownBy(() -> {
			profileImageRepository.save(profileImage);
		}).isInstanceOf(ConstraintViolationException.class);
	}

	@Test
	@DisplayName("경로 없이 프로필을 저장하려 할때 Validation 오류가 난다.")
	void testSaveNotContainPath() {
		// given
		ProfileImage profileImage = ProfileImage.builder()
			.member(getDemoMember())
			.originalFileName(originalFileName)
			.serverFileName(serverFileName)
			.size(size)
			.build();

		// when
		// then
		Assertions.assertThatThrownBy(() -> {
			profileImageRepository.save(profileImage);
		}).isInstanceOf(ConstraintViolationException.class);
	}

	@Test
	@DisplayName("memberId로 프로필 조회하기")
	void testFindByMember() {
		//given
		Member demoMember = getDemoMember();
		ProfileImage profileImage = ProfileImage.builder()
			.member(demoMember)
			.originalFileName(originalFileName)
			.serverFileName(serverFileName)
			.size(size)
			.path(path)
			.build();

		profileImageRepository.save(profileImage);
		testEntityManager.flush();

		//when
		Optional<ProfileImage> resultProfileImage = profileImageRepository.findByMember(demoMember);
		//then

		Assertions.assertThat(resultProfileImage.isEmpty()).isFalse();
		MatcherAssert.assertThat(resultProfileImage.get(), Matchers.samePropertyValuesAs(profileImage));
	}

	private Member getDemoMember() {
		String name = "programmer";
		Member demoMember = Member.builder()
			.name(name)
			.username(name + "123")
			.email(name + "@programmers.co.kr")
			.password("Programmer123!")
			.phoneNumber("01012341234")
			.introduction("hi velopert")
			.build();

		testEntityManager.persist(demoMember);

		return demoMember;
	}
}
*/
