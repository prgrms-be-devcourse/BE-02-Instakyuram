package com.kdt.instakyuram.post.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

	List<PostLike> findByPostId(Long postId);
}
