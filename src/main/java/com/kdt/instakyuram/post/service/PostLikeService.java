package com.kdt.instakyuram.post.service;

import org.springframework.stereotype.Service;

import com.kdt.instakyuram.member.dto.MemberResponse;
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

	public List<PostLikeResponse> findByPostId(Long postId) {
		return postLikeRepository.findByPostId(postId).stream()
			.map(postLike -> PostLikeResponse.builder()
				.postId(postId)
				.likes(postLikeRepository.countByPostId(postId))
				.build())
			.toList();
	}

	public int countByPostId(Long postId) {
		return postLikeRepository.countByPostId(postId);
	}

	public PostLikeResponse like(PostResponse post, MemberResponse member) {
		if (postLikeRepository.existsPostLikeByPostIdAndMemberId(post.id(), member.id())) {
			throw new IllegalArgumentException("이미 좋아요 상태입니다.");
		}

		postLikeRepository.save(postConverter.toPostLike(post, member));
		int likes = postLikeRepository.countByPostId(post.id());

		return new PostLikeResponse(post.id(), likes, true);
	}

	public PostLikeResponse unlike(PostResponse post, MemberResponse member) {
		return postLikeRepository.findByPostIdAndMemberId(post.id(), member.id())
			.map(postLike -> {
				postLikeRepository.delete(postLike);
				int likes = postLikeRepository.countByPostId(post.id());

				return new PostLikeResponse(post.id(), likes, false);
			})
			.orElseThrow(() -> new IllegalArgumentException("이미 좋아요 취소 상태입니다. "));
	}
}
