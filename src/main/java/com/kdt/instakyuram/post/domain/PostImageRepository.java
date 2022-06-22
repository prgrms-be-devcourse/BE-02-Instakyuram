package com.kdt.instakyuram.post.domain;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PostImageRepository extends JpaRepository<PostImage, Long> {

	List<PostImage> findByPostId(Long postId);

	Optional<PostImage> findByServerFileName(String serverFileName);

	Optional<PostImage> findTop1ByPostId(Long postId);

}
