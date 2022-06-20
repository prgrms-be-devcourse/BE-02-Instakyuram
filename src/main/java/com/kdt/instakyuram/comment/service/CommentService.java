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

	public CommentService(CommentRepository commentRepository, CommentConverter commentConverter, MemberGiver memberGiver) {
		this.commentRepository = commentRepository;
		this.commentConverter = commentConverter;
		this.memberGiver = memberGiver;
	}

	@Transactional
	public CommentResponse create(Long postId, Long memberId, String content) {
		MemberResponse member = memberGiver.findById(memberId);
		Comment comment = commentConverter.toComment(member, postId, content);
		Comment savedComment = commentRepository.save(comment);

		return new CommentResponse(
			savedComment.getId(),
			savedComment.getContent(),
			member
		);
	}

	@Transactional
	public CommentResponse.UpdateResponse update(Long id, Long postId, Long memberId, String content) {
		return commentRepository.findByIdAndPostIdAndMemberId(id, postId, memberId)
			.map(comment -> {
				comment.changeContent(content);

				return new CommentResponse.UpdateResponse(id, content);
			})
			.orElseThrow(() -> new NotFoundException("해당 댓글은 존재하지 않습니다."));
	}

	@Override
	public List<CommentResponse> findByPostId(Long postId) {
		return commentRepository.findAllByPostId(postId).stream()
			.map(commentConverter::toResponse)
			.toList();
	}
}
