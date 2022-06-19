package com.kdt.instakyuram.post.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kdt.instakyuram.post.domain.PostLikeRepository;
import com.kdt.instakyuram.post.dto.PostConverter;
import com.kdt.instakyuram.post.dto.PostLikeResponse;

@Service
@Transactional(readOnly = true)
public class PostLikeService {
	private final PostLikeRepository postLikeRepository;
	private final PostConverter postConverter;

	public PostLikeService(PostLikeRepository postLikeRepository, PostConverter postConverter) {
		this.postLikeRepository = postLikeRepository;
		this.postConverter = postConverter;
	}

	public List<PostLikeResponse> findByPostId(Long postId) {
		return postLikeRepository.findByPostId(postId).stream()
			.map(postConverter::toPostLikeResponse)
			.toList();
	}

	public int countByPostId(Long postId) {
		return postLikeRepository.countByPostId(postId);
	}

}
