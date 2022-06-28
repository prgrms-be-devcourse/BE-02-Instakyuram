package com.kdt.instakyuram.article.postimage.domain;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kdt.instakyuram.article.post.domain.Post;

public interface PostImageRepository extends JpaRepository<PostImage, Long> {

	List<PostImage> findByPostId(Long postId);

	Optional<PostImage> findByServerFileName(String serverFileName);

	Optional<PostImage> findTop1ByPostId(Long postId);

	List<PostImage> findByPostIn(List<Post> posts);
}
