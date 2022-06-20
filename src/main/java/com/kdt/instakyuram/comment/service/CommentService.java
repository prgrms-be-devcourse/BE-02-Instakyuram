package com.kdt.instakyuram.comment.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kdt.instakyuram.comment.domain.Comment;
import com.kdt.instakyuram.comment.domain.CommentRepository;
import com.kdt.instakyuram.comment.dto.CommentConverter;
import com.kdt.instakyuram.comment.dto.CommentResponse;
import com.kdt.instakyuram.exception.NotFoundException;
import com.kdt.instakyuram.member.dto.MemberResponse;
import com.kdt.instakyuram.member.service.MemberGiver;

@Service
public class CommentService implements CommentGiver {

	private final CommentRepository commentRepository;
	private final CommentConverter commentConverter;
	private final MemberGiver memberGiver;
	private final CommentLikeService commentLikeService;

	public CommentService(
		CommentRepository commentRepository,
		CommentConverter commentConverter,
		MemberGiver memberGiver,
		CommentLikeService commentLikeService
	) {
		this.commentRepository = commentRepository;
		this.commentConverter = commentConverter;
		this.memberGiver = memberGiver;
		this.commentLikeService = commentLikeService;
	}

	@Transactional
	public CommentResponse create(Long postId, Long memberId, String content) {
		MemberResponse memberResponse = memberGiver.findById(memberId);
		Comment comment = commentConverter.toComment(memberResponse, postId, content);
		Comment savedComment = commentRepository.save(comment);

		return new CommentResponse(
			savedComment.getId(),
			savedComment.getContent(),
			memberResponse
		);
	}

	@Transactional
	public CommentResponse.LikeResponse like(Long id, Long memberId) {
		return commentRepository.findById(id)
			.map(comment -> {
				MemberResponse memberResponse = memberGiver.findById(memberId);
				CommentResponse commentResponse = commentConverter.toResponse(comment);

				return commentLikeService.like(commentResponse, memberResponse);
			})
			.orElseThrow(() -> new NotFoundException("존재하지 않는 댓글입니다."));
	}

	@Transactional
	public CommentResponse.LikeResponse unlike(Long id, Long memberId) {
		return commentRepository.findById(id)
			.map(comment -> commentLikeService.unlike(id, memberId))
			.orElseThrow(() -> new NotFoundException("존재하지 않는 댓글입니다."));
	}

	@Override
	public List<CommentResponse> findByPostId(Long postId) {
		return commentRepository.findAllByPostId(postId).stream()
			.map(commentConverter::toResponse)
			.toList();
	}


}
