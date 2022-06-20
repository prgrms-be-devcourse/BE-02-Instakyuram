package com.kdt.instakyuram.comment.domain;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {

	boolean existsCommentLikeByCommentIdAndMemberId(Long commentId, Long memberId);

	@Query("SELECT count(cl.id) FROM CommentLike cl")
	int countByCommentId(Long commentId);

	@Query("SELECT cl FROM CommentLike cl WHERE cl.comment.id = :commentId AND cl.member.id = :memberId")
	Optional<CommentLike> findByCommentIdAndMemberId(Long commentId, Long memberId);
}
