package com.kdt.instakyuram.comment.domain;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.kdt.instakyuram.post.domain.Post;

public interface CommentRepository extends JpaRepository<Comment, Long>, CommentRepositoryCustom {

	@Query("SELECT c FROM Comment c JOIN FETCH c.member m WHERE c.post.id = :postId")
	List<Comment> findAllByPostId(Long postId);

	@Query("SELECT c FROM Comment c WHERE c.id = :id AND c.member.id = :memberId")
	Optional<Comment> findByIdAndMemberId(Long id, Long memberId);

	List<Comment> findByPostIn(List<Post> posts);
}
