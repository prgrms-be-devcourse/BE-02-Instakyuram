package com.kdt.instakyuram.comment.service;

import java.util.List;

import com.kdt.instakyuram.comment.dto.CommentResponse;
import com.kdt.instakyuram.post.domain.Post;

public interface CommentGiver {
	List<CommentResponse> findByPostId(Long postId);

	void delete(Long id);

	List<CommentResponse> findByPostIn(List<Post> posts);
}
