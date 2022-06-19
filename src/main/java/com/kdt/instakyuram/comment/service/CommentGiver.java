package com.kdt.instakyuram.comment.service;

import java.util.List;

import com.kdt.instakyuram.comment.dto.CommentResponse;

public interface CommentGiver {
	List<CommentResponse> findByPostId(Long postId);
}
