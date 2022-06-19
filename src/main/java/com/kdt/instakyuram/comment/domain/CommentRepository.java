package com.kdt.instakyuram.comment.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CommentRepository extends JpaRepository<Comment, Long> {

	@Query("SELECT c FROM Comment c JOIN FETCH c.member m WHERE c.post.id = :postId")
	List<Comment> findAllByPostId(Long postId);
}
