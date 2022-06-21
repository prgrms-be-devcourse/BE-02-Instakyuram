package com.kdt.instakyuram.post.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kdt.instakyuram.post.domain.PostLike;
import com.kdt.instakyuram.post.domain.PostLikeRepository;
import com.kdt.instakyuram.post.dto.PostConverter;
import com.kdt.instakyuram.post.dto.PostLikeResponse;
import com.kdt.instakyuram.post.dto.PostResponse;

@Service
@Transactional(readOnly = true)
public class PostLikeService {
	private final PostLikeRepository postLikeRepository;
	private final PostConverter postConverter;

	public PostLikeService(PostLikeRepository postLikeRepository, PostConverter postConverter) {
		this.postLikeRepository = postLikeRepository;
		this.postConverter = postConverter;
	}

	public int countByPostId(Long postId) {
		return postLikeRepository.countByPostId(postId);
	}

	@Transactional
	public PostLikeResponse like(Long postId, Long memberId) {
		if (postLikeRepository.existsPostLikeByPostIdAndMemberId(postId, memberId)) {
			throw new IllegalArgumentException("이미 좋아요 상태입니다.");
		}

		postLikeRepository.save(postConverter.toPostLike(postId, memberId));
		int likes = postLikeRepository.countByPostId(postId);

		return new PostLikeResponse(postId, likes, true);
	}

	@Transactional
	public PostLikeResponse unlike(PostResponse post, Long memberId) {
		return postLikeRepository.findByPostIdAndMemberId(post.id(), memberId)
			.map(postLike -> {
				postLikeRepository.delete(postLike);
				int likes = postLikeRepository.countByPostId(post.id());

				return new PostLikeResponse(post.id(), likes, false);
			})
			.orElseThrow(() -> new IllegalArgumentException("이미 좋아요 취소 상태입니다. "));
	}

	@Transactional
	public void delete(Long id) {
		List<PostLike> postLikes = postLikeRepository.findByPostId(id);
		postLikeRepository.deleteAll(postLikes);
	}
}
