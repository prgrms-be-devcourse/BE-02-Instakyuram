package com.kdt.instakyuram.article.post.domain;

import java.util.List;
import java.util.Optional;

import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.kdt.instakyuram.user.member.domain.Member;

public interface PostRepository extends JpaRepository<Post, Long> {

	List<Post> findAllByMemberIn(List<Member> members);

	Optional<Post> findByIdAndMemberId(Long id, Long memberId);

	List<Post> findAllByMemberId(Long memberId);

	@Query("SELECT p FROM Post p JOIN FETCH p.member m WHERE m.username = :username")
	List<Post> findAllByUsername(@Param("username") String username);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT p FROM Post p WHERE p.id = :id AND p.member.id = :memberId")
	Optional<Post> findByIdAndMemberId_Locked_Pessimistic(Long id, Long memberId);

}
