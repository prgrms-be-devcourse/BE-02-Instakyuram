package com.kdt.instakyuram.comment.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.kdt.instakyuram.comment.domain.CommentLike;
import com.kdt.instakyuram.comment.domain.CommentLikeRepository;
import com.kdt.instakyuram.comment.dto.CommentConverter;
import com.kdt.instakyuram.comment.dto.CommentResponse;
import com.kdt.instakyuram.member.dto.MemberResponse;

@Service
public class CommentLikeService {

	private final CommentLikeRepository commentLikeRepository;
	private final CommentConverter commentConverter;

	public CommentLikeService(CommentLikeRepository commentLikeRepository, CommentConverter commentConverter) {
		this.commentLikeRepository = commentLikeRepository;
		this.commentConverter = commentConverter;
	}

	public CommentResponse.LikeResponse like(CommentResponse comment, MemberResponse member) {
		if (commentLikeRepository.existsCommentLikeByCommentIdAndMemberId(comment.id(), member.id())) {
			throw new IllegalArgumentException("이미 좋아요 상태 입니다.");
		}

		CommentLike commentLike = commentConverter.toCommentLike(comment, member);
		commentLikeRepository.save(commentLike);

		// 해당 댓글의 좋아요 개수까지 포함해서 리턴
		int likes = commentLikeRepository.countByCommentId(comment.id());

		return new CommentResponse.LikeResponse(comment.id(), likes, true);
	}

	public CommentResponse.LikeResponse unlike(Long id, Long memberId) {
		return commentLikeRepository.findByCommentIdAndMemberId(id, memberId)
			.map(commentLike -> {
				commentLikeRepository.delete(commentLike);

				// 해당 댓글의 좋아요 개수까지 포함해서 리턴
				int likes = commentLikeRepository.countByCommentId(id);

				return new CommentResponse.LikeResponse(id, likes, false);
			})
			.orElseThrow(() -> new IllegalArgumentException("이미 좋아요 취소 상태 입니다."));
	}

	public void delete(Long commentId) {
		List<CommentLike> commentLikes = commentLikeRepository.findByCommentId(commentId);
		commentLikeRepository.deleteAll(commentLikes);
	}
}
