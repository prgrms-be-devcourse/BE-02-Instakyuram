package com.kdt.instakyuram.post.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PostImageRepository extends JpaRepository<PostImage, Long> {

	List<PostImage> findByPostId(Long postId);
}
