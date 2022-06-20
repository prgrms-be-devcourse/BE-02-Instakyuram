package com.kdt.instakyuram.comment.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {

	boolean existsCommentLikeByCommentIdAndMemberId(Long commentId, Long memberId);

	@Query("SELECT count(cl.id) FROM CommentLike cl")
	int countByCommentId(Long commentId);
}
