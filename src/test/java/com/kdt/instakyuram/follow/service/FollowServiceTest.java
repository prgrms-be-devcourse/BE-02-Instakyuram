package com.kdt.instakyuram.follow.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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

import com.kdt.instakyuram.exception.EntityNotFoundException;
import com.kdt.instakyuram.follow.domain.Follow;
import com.kdt.instakyuram.follow.domain.FollowRepository;

@ExtendWith(MockitoExtension.class)
class FollowServiceTest {

	@InjectMocks
	private FollowService followService;

	@Mock
	private FollowRepository followRepository;

	@Test
	@DisplayName("팔로잉한 목록 조회 테스트 [1L의 식별자를 가진 사용자가 2L,3L을 가진 멤버를 팔로잉 할때]")
	void testGettingFollowingIds() {
		Long memberId = 1L;
		Long targetAId = 2L;
		Long targetBId = 3L;

		List<Follow> followingTargetIds = List.of(
			Follow.builder()
				.memberId(memberId)
				.targetId(targetAId).
				build(),
			Follow.builder()
				.memberId(memberId)
				.targetId(targetBId)
				.build()
		);

		//given
		given(followRepository.findByMemberId(memberId)).willReturn(followingTargetIds);

		//when
		List<Long> followingIds = followService.findByFollowingIds(memberId);

		//then
		assertThat(followingIds.size()).isEqualTo(followingTargetIds.size());
		assertThat(followingIds).contains(targetAId, targetBId);

		verify(followRepository, times(1)).findByMemberId(memberId);
	}

	@Test
	@DisplayName("나를 따르는 사람의 수 구하기 =: follower 수")
	void testCountMyFollower() {
		//given
		Long expectedMyFollower = 10L;
		Long myId = 1L;

		given(followRepository.countByTargetId(myId)).willReturn(expectedMyFollower);

		//when
		Long myFollower = followRepository.countByTargetId(myId);

		//then
		Assertions.assertNotNull(myFollower);
		assertThat(myFollower).isEqualTo(expectedMyFollower);

		verify(followRepository, times(1)).countByTargetId(myId);
	}

	@Test
	@DisplayName("내가 따르는 사람의 수 구하기 =: following 수")
	void testCountMyFollowing() {
		//given
		Long expectedMyFollower = 10L;
		Long myId = 1L;

		given(followRepository.countByTargetId(myId)).willReturn(expectedMyFollower);

		//when
		Long myFollowing = followRepository.countByTargetId(myId);

		//then
		Assertions.assertNotNull(myFollowing);
		assertThat(myFollowing).isEqualTo(expectedMyFollower);

		verify(followRepository, times(1)).countByTargetId(myId);
	}

	@Test
	@DisplayName("팔로우 할 수 있는 대상인지? true-팔로우 할 수 있음")
	void testPossibleFollow() {
		//given
		Long memberId = 1L;
		Long targetId = 2L;
		boolean notExist = false;

		given(followRepository.existsByMemberIdAndTargetId(memberId, targetId)).willReturn(notExist);

		//when
		boolean isFollow = followService.isFollowed(memberId, targetId);

		//then
		assertThat(isFollow).isTrue();

		verify(followRepository, times(1)).existsByMemberIdAndTargetId(memberId, targetId);
	}

	@Test
	@DisplayName("팔로우 할 수 있는 대상인지? false-할수 없음")
	void testNoPossibleFollow() {
		//given
		Long memberId = 1L;
		Long targetId = 2L;

		boolean exist = true;
		given(followRepository.existsByMemberIdAndTargetId(memberId, targetId)).willReturn(exist);

		//when
		boolean isFollow = followService.isFollowed(1L, 2L);

		//then
		assertThat(isFollow).isFalse();

		verify(followRepository, times(1)).existsByMemberIdAndTargetId(memberId, targetId);
	}

	@Test
	@DisplayName("언팔로우 테스트")
	void testUnFollow() {
		//given
		Long memberId = 1L;
		Long targetId = 2L;

		Follow follow = Follow.builder()
			.memberId(memberId)
			.targetId(targetId)
			.build();

		given(followRepository.findByMemberIdAndTargetId(memberId, targetId)).willReturn(Optional.of(follow));

		//when
		followService.unFollow(memberId, targetId);

		//then
		verify(followRepository, times(1)).findByMemberIdAndTargetId(memberId, targetId);
	}

	@Test
	@DisplayName("언팔로우 테스트 : 팔로우한 기록이 없을 때 예외를 발생시킨디.")
	void testFailUnFollow() {
		//given
		Long memberId = 1L;
		Long targetId = 2L;

		given(followRepository.findByMemberIdAndTargetId(memberId, targetId)).willReturn(Optional.empty());

		//when
		//then
		assertThatThrownBy(() -> {
			followService.unFollow(memberId, targetId);
		}).isInstanceOf(EntityNotFoundException.class);

		verify(followRepository, times(1)).findByMemberIdAndTargetId(memberId, targetId);
	}

	@Test
	@DisplayName("나를 팔로우한 팔로워 찾아오기 [이미 봤던 사람 이후로 30개씩] - 2번째 팔로워 대상들")
	void testFindByFollower() {
		//given
		Long targetId = 1L;
		Long alreadyLookUpMemberId = 30L;

		List<Follow> nextFollowerIds = LongStream.rangeClosed(alreadyLookUpMemberId + 1, alreadyLookUpMemberId + 60)
			.boxed()
			.map(value -> Follow.builder().targetId(targetId).memberId(value).build())
			.toList();

		given(
			followRepository.findTop30ByTargetIdAndMemberIdGreaterThanOrderByMemberId(targetId, alreadyLookUpMemberId))
			.willReturn(nextFollowerIds);

		//when
		followService.findByMyFollower(targetId, alreadyLookUpMemberId);

		//then
		verify(followRepository, times(1)).findTop30ByTargetIdAndMemberIdGreaterThanOrderByMemberId(targetId,
			alreadyLookUpMemberId);
	}

	@Test
	@DisplayName("내가 팔로잉한 사람들 찾아오기 [이미 봤던 사람 이후로 30개씩] - 2번째 팔로잉 대상들")
	void testFindByFollowings() {
		//given
		Long memberId = 1L;
		Long alreadyLookUpMemberId = 30L;

		List<Follow> nextFollowerIds = LongStream.rangeClosed(alreadyLookUpMemberId + 1, alreadyLookUpMemberId + 60)
			.boxed()
			.map(value -> Follow.builder().targetId(value).memberId(memberId).build())
			.toList();

		given(
			followRepository.findTop30ByMemberIdAndTargetIdGreaterThanOrderByTargetId(memberId, alreadyLookUpMemberId))
			.willReturn(nextFollowerIds);

		//when
		List<Long> followings = followService.findByMyFollowings(memberId, alreadyLookUpMemberId);

		//then
		verify(followRepository, times(1)).findTop30ByMemberIdAndTargetIdGreaterThanOrderByTargetId(memberId,
			alreadyLookUpMemberId);
	}

	@Test
	@DisplayName("인증된 사용자가 팔로잉한 목록 조회하기 [인증된 사용자가 타 팔로잉 한 사람이 있는경우 ui에 언팔로우 버튼이 활성화 되어야함]")
	void testGetAuthFollowings() {
		//given
		Long authId = 2L;
		List<Long> promisingFollowings = LongStream.rangeClosed(1, 10).boxed().toList();
		List<Follow> authFollowings = List.of(
			Follow.builder()
				.memberId(authId)
				.targetId(3L)
				.build(),
			Follow.builder()
				.memberId(authId)
				.targetId(7L)
				.build()
		);

		Set<Follow> expectedAuthFollowing = new HashSet<>(authFollowings);

		given(followRepository.findByMemberIdAndTargetIdIn(authId, promisingFollowings)).willReturn(
			authFollowings);

		//when
		Set<Long> authfollowing = followService.findAuthFollowings(authId, promisingFollowings);

		//then
		assertThat(authfollowing.size()).isEqualTo(expectedAuthFollowing.size());
		MatcherAssert.assertThat(authfollowing, Matchers.samePropertyValuesAs(expectedAuthFollowing));

		verify(followRepository, times(1)).findByMemberIdAndTargetIdIn(authId, promisingFollowings);
	}
}