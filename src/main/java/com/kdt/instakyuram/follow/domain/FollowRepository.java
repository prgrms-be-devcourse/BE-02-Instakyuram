package com.kdt.instakyuram.follow.domain;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowRepository extends JpaRepository<Follow, Long> {

	// todo : 리밋 다시 한번 생각해봐야함(서버 메모리).
	List<Follow> findByMemberId(Long memberId);

	Long countByMemberId(Long myId);

	Long countByTargetId(Long myId);

	Optional<Follow> findByMemberIdAndTargetId(Long memberId, Long targetId);

	boolean existsByMemberIdAndTargetId(Long memberId, Long targetId);

	List<Follow> findTop30ByTargetIdAndMemberIdGreaterThanOrderByMemberId(Long targetId, Long memberId);

	List<Follow> findTop30ByMemberIdAndTargetIdGreaterThanOrderByTargetId(Long memberId,Long targetId);

	List<Follow> findByMemberIdAndTargetIdIn(Long memberId, List<Long> targetIds);
}
