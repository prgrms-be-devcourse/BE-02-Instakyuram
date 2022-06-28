package com.kdt.instakyuram.user.follow.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import javax.validation.ConstraintViolationException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;

import com.kdt.instakyuram.exception.DomainException;
import com.kdt.instakyuram.user.follow.domain.Follow;
import com.kdt.instakyuram.user.follow.domain.FollowRepository;

@DataJpaTest
class FollowTest {

	@Autowired
	private TestEntityManager entityManager;

	@Autowired
	private FollowRepository followRepository;

	@Test
	@DisplayName("memberId(팔로잉한 id)가 비어있을 때 validation 오류가 발생한다.")
	void testSaveNotContainMemberId() {
		//given
		//when
		//then
		assertThatThrownBy(() -> {
			Follow.builder()
				.memberId(1L)
				.build();
		}).isInstanceOf(DomainException.class)
			.hasMessage("팔로우 하는 대상과 팔로우 받는 대상은 반드시 필요합니다.");
	}

	@Test
	@DisplayName("자신을 팔로우 하는 경우 오류가 발생한다.")
	void testSaveSameMemberIdAndTargetId() {
		//given
		Long myId = 1L;

		//when
		//then
		assertThatThrownBy(() ->
			Follow.builder()
				.memberId(myId)
				.targetId(myId)
				.build()
		)
			.isInstanceOf(DomainException.class)
			.isInstanceOf(DomainException.class)
			.hasMessage("자신을 팔로우 할 수는 없습니다.");

	}

	@Test
	@DisplayName("팔로우 하는 대상 id에 음수의 값이 들어가면 validation 오류가 발생한다.")
	void testMinusIdInMemberId() {
		//given
		Follow follow = Follow.builder()
			.memberId(-1L)
			.targetId(2L)
			.build();

		//when
		//then
		assertThatThrownBy(() -> {
			followRepository.save(follow);
		}).isInstanceOf(ConstraintViolationException.class);
	}

	@Test
	@DisplayName("팔로우 받는 는상 id에 음수의 값이 들어가면 validation 오류가 발생한다.")
	void testMinusIdInTargetId() {
		//given
		Follow follow = Follow.builder()
			.memberId(-1L)
			.targetId(2L)
			.build();

		//when
		//then
		assertThatThrownBy(() -> {
			followRepository.save(follow);
		}).isInstanceOf(ConstraintViolationException.class);
	}

	@Test
	@DisplayName("이미 팔로잉 되어 있는 상태인데 또 팔로잉하는 경우 예외가 발생한다. [unique constraint]")
	void testSameFollow() {
		//given
		Long memberId = 1L;
		Long targetId = 2L;

		Follow follow = Follow.builder()
			.memberId(memberId)
			.targetId(targetId)
			.build();

		followRepository.save(follow);

		Follow overWrittenFollow = Follow.builder()
			.memberId(memberId)
			.targetId(targetId)
			.build();

		//when
		//then
		assertThatThrownBy(() -> {
			followRepository.save(overWrittenFollow);
		}).isInstanceOf(DataIntegrityViolationException.class);
	}

}