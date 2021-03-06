package com.kdt.instakyuram.user.member.domain;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberRepository extends JpaRepository<Member, Long>, CustomMemberRepository {

	List<Member> findByIdIn(List<Long> memberIds);

	List<Member> findByIdInOrderById(List<Long> memberIds);

	Optional<Member> findByUsername(String username);

	@Query("SELECT m FROM Member m WHERE m.id IN :ids OR m.id = :id")
	List<Member> findAllIdsInOrById(@Param("ids") List<Long> ids, @Param("id") Long id);

	Page<Member> findAllByIdNot(Long id, Pageable pageable);
}
