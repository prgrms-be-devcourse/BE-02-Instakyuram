package com.kdt.instakyuram.follow.follow.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kdt.instakyuram.follow.domain.Follow;
import com.kdt.instakyuram.follow.domain.FollowRepository;
import com.kdt.instakyuram.follow.service.FollowService;

@ExtendWith(MockitoExtension.class)
class FollowServiceTest {

	@InjectMocks
	private FollowService followService;

	@Mock
	private FollowRepository followRepository;

	@Test
	@DisplayName("의존성 주입 테스트")
	void testDependencyInjection() {
		//given
		//when
		//then
		Assertions.assertNotNull(followService);
		Assertions.assertNotNull(followRepository);
	}

	@Test
	@DisplayName("팔로잉한 목록 조회 테스트 [1L의 식별자를 가진 사용자가 2L,3L을 가진 멤버를 팔로잉 할때]")
	void testGettingFollowingIds() {
		Long followingMemberId = 1L;
		Long followingToTargetFirst = 2L;
		Long followingToTargetSecond = 3L;

		List<Follow> followingTargetIds = List.of(
			Follow.builder()
				.memberId(followingMemberId)
				.targetId(followingToTargetFirst).
				build(),
			Follow.builder()
				.memberId(followingMemberId)
				.targetId(followingToTargetSecond)
				.build());

		//given
		BDDMockito.given(followRepository.findByMemberId(followingMemberId)).willReturn(followingTargetIds);
		//when
		List<Long> followingIds = followService.findByFollowingIds(followingMemberId);
		//then

		assertThat(followingIds.size()).isEqualTo(followingTargetIds.size());
		assertThat(followingIds).contains(followingToTargetFirst, followingToTargetSecond);
	}

}