package com.kdt.instakyuram.post.domain;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

	List<PostLike> findByPostId(Long postId);

	int countByPostId(Long postId);

	Optional<PostLike> findByPostIdAndMemberId(Long postId, Long memberId);

	boolean existsPostLikeByPostIdAndMemberId(Long postId, Long memberId);

	List<PostLike> findByPostIn(List<Post> posts);
}
