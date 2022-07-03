package com.kdt.instakyuram.article.comment.service;

import java.text.MessageFormat;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kdt.instakyuram.article.comment.domain.Comment;
import com.kdt.instakyuram.article.comment.domain.CommentRepository;
import com.kdt.instakyuram.article.comment.dto.CommentConverter;
import com.kdt.instakyuram.article.comment.dto.CommentFindAllResponse;
import com.kdt.instakyuram.article.comment.dto.CommentResponse;
import com.kdt.instakyuram.article.post.domain.Post;
import com.kdt.instakyuram.exception.EntityNotFoundException;
import com.kdt.instakyuram.exception.ErrorCode;
import com.kdt.instakyuram.user.member.dto.MemberResponse;
import com.kdt.instakyuram.user.member.service.MemberGiver;

@Transactional(readOnly = true)
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
			postId,
			savedComment.getContent(),
			memberResponse
		);
	}

	// TODO [COMMENT] 비관적 락 테스트
	@Transactional
	public CommentResponse modify(Long id, Long memberId, String content) {
		return commentRepository.findByIdAndMemberId_Locked_Pessimistic(id, memberId)
			.map(comment -> {
				comment.modify(content);

				return commentConverter.toResponse(comment);
			})
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.COMMENT_NOT_FOUND,
				MessageFormat.format("id = {0} memberId= {1}", id, memberId)));
	}

	@Transactional
	public void delete(Long id, Long memberId) {
		commentRepository.findByIdAndMemberId(id, memberId)
			.map(comment -> {
				commentLikeService.delete(id);
				commentRepository.delete(comment);
				return true;
			})
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.COMMENT_NOT_FOUND,
				MessageFormat.format("id = {0} memberId = {1}", id, memberId)));
	}

	@Transactional
	public CommentResponse.LikeResponse like(Long id, Long memberId) {
		return commentRepository.findById(id)
			.map(comment -> {
				MemberResponse memberResponse = memberGiver.findById(memberId);
				CommentResponse commentResponse = commentConverter.toResponse(comment);

				return commentLikeService.like(commentResponse, memberResponse);
			})
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.COMMENT_NOT_FOUND));
	}

	@Transactional
	public CommentResponse.LikeResponse unlike(Long id, Long memberId) {
		return commentRepository.findById(id)
			.map(comment -> commentLikeService.unlike(id, memberId))
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.COMMENT_NOT_FOUND,
				MessageFormat.format("id = {0} memberId = {1}", id, memberId)));
	}

	@Override
	public List<CommentResponse> findByPostId(Long postId) {
		return commentRepository.findAllByPostId(postId).stream()
			.map(commentConverter::toResponse)
			.toList();
	}

	@Transactional
	@Override
	public void delete(Long id) {
		commentRepository.findById(id)
			.map(comment -> {
				commentLikeService.delete(id);
				commentRepository.delete(comment);
				return true;
			})
			.orElseThrow(
				() -> new EntityNotFoundException(ErrorCode.COMMENT_NOT_FOUND, MessageFormat.format("id = {0}", id)));
	}

	@Override
	public List<CommentResponse> findByPostIn(List<Post> posts) {
		return commentRepository.findByPostIn(posts).stream()
			.map(commentConverter::toResponse)
			.toList();
	}

	public List<CommentFindAllResponse> findAll(Long postId, Long memberId) {
		return commentRepository.findAllByPostIdAndMemberId(postId, memberId);
	}
}
