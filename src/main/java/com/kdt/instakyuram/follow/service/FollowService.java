package com.kdt.instakyuram.follow.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kdt.instakyuram.exception.NotFoundException;
import com.kdt.instakyuram.follow.domain.Follow;
import com.kdt.instakyuram.follow.domain.FollowRepository;

@Transactional(readOnly = true)
@Service
public class FollowService {
	private final FollowRepository followRepository;

	public FollowService(FollowRepository followRepository) {
		this.followRepository = followRepository;
	}

	// todo : limit 제한 다시 생각하기
	public List<Long> findByFollowingIds(Long id) {
		return followRepository.findByMemberId(id).stream()
			.map(Follow::getTargetId)
			.toList();
	}

	public Long countMyFollowing(Long memberId) {
		return followRepository.countByMemberId(memberId);
	}

	public Long countMyFollower(Long memberId) {
		return followRepository.countByTargetId(memberId);
	}

	public boolean isFollowed(Long memberId, Long targetId) {
		return !followRepository.existsByMemberIdAndTargetId(memberId, targetId);
	}

	@Transactional
	public void follow(Long id, Long targetId) {
		followRepository.save(Follow.builder()
			.memberId(id)
			.targetId(targetId)
			.build()
		);
	}

	@Transactional
	public void unFollow(Long id, Long targetId) {
		followRepository.delete(
			followRepository.findByMemberIdAndTargetId(id, targetId)
				.orElseThrow(NotFoundException::new)
		);
	}

	public List<Long> findByMyFollower(Long targetId, Long alreadyLookUpIndex) {
		return followRepository.findTop30ByTargetIdAndMemberIdGreaterThanOrderByMemberId(targetId, alreadyLookUpIndex)
			.stream()
			.map(Follow::getMemberId)
			.toList();
	}

	public List<Long> findByMyFollowings(Long memberId, Long alreadyLookUpIndex) {
		return followRepository.findTop30ByMemberIdAndTargetIdGreaterThanOrderByTargetId(memberId, alreadyLookUpIndex)
			.stream()
			.map(Follow::getTargetId)
			.toList();

	}

	public Set<Long> getAuthFollowings(Long authId, List<Long> promisingFollowings) {
		return followRepository.findByMemberIdAndTargetIdIn(authId, promisingFollowings).stream()
			.map(Follow::getTargetId)
			.collect(Collectors.toSet());
	}
}
