package com.kdt.instakyuram.member.domain;

import java.util.Optional;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
	Optional<Member> findByUsername(String username);

	List<Member> findByIdIn(List<Long> memberIds);
}
