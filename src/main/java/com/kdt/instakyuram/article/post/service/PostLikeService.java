package com.kdt.instakyuram.article.post.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kdt.instakyuram.exception.BusinessException;
import com.kdt.instakyuram.exception.ErrorCode;
import com.kdt.instakyuram.article.post.domain.Post;
import com.kdt.instakyuram.article.post.domain.PostLike;
import com.kdt.instakyuram.article.post.domain.PostLikeRepository;
import com.kdt.instakyuram.article.post.dto.PostConverter;
import com.kdt.instakyuram.article.post.dto.PostLikeResponse;
import com.kdt.instakyuram.article.post.dto.PostResponse;

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
			throw new BusinessException(ErrorCode.POST_ALREADY_LIKED);
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
			.orElseThrow(() -> new BusinessException(ErrorCode.POST_ALREADY_UNLIKE));
	}

	@Transactional
	public void delete(Long postId) {
		List<PostLike> postLikes = postLikeRepository.findByPostId(postId);
		postLikeRepository.deleteAll(postLikes);
	}

	public List<PostLikeResponse> findByPostIn(List<Post> posts) {
		return postLikeRepository.findByPostIn(posts).stream()
			.map(postConverter::toPostLikeResponse)
			.toList();
	}
}
