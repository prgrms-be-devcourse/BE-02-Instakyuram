package com.kdt.instakyuram.follow.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
		return followRepository.findByMemberId(id);
	}
}
