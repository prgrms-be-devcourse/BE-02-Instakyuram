package com.kdt.instakyuram.member.domain;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberRepository extends JpaRepository<Member, Long> {

	List<Member> findByIdIn(List<Long> memberIds);

	Optional<Member> findByUsername(String username);

	@Query("SELECT m FROM Member m WHERE m.id IN :ids OR m.id = :id")
	List<Member> findAllIdsInOrById(@Param("ids") List<Long> ids, @Param("id") Long id);
}
