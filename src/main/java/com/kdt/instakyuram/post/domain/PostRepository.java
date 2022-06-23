package com.kdt.instakyuram.post.domain;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.kdt.instakyuram.member.domain.Member;

public interface PostRepository extends JpaRepository<Post, Long> {

	List<Post> findByMemberIn(List<Member> members);

	Optional<Post> findByIdAndMemberId(Long id, Long memberId);

	List<Post> findAllByMemberId(Long memberId);

	@Query("SELECT p FROM Post p JOIN FETCH p.member m WHERE m.username = :username")
	List<Post> findAllByUsername(@Param("username") String username);
}
