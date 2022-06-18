package com.kdt.instakyuram.follow.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface FollowRepository extends JpaRepository<Follow, Long> {

	// todo : 리밋 다시 한번 생각해봐야함(서버 메모리).
	List<Long> findByMemberId(@Param("memberId") Long memberId);
}
