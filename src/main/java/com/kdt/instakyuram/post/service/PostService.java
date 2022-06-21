package com.kdt.instakyuram.post.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.kdt.instakyuram.comment.service.CommentGiver;
import com.kdt.instakyuram.exception.NotFoundException;
import com.kdt.instakyuram.member.domain.Member;
import com.kdt.instakyuram.member.dto.MemberResponse;
import com.kdt.instakyuram.member.service.MemberGiver;
import com.kdt.instakyuram.post.domain.Post;
import com.kdt.instakyuram.post.domain.PostRepository;
import com.kdt.instakyuram.post.dto.PostConverter;
import com.kdt.instakyuram.post.dto.PostLikeResponse;
import com.kdt.instakyuram.post.dto.PostResponse;

@Transactional(readOnly = true)
@Service
public class PostService {

	private final PostRepository postRepository;
	private final PostConverter postConverter;
	private final MemberGiver memberGiver;
	private final PostImageService postImageService;
	private final CommentGiver commentGiver;
	private final PostLikeService postLikeService;

	public PostService(PostRepository postRepository, PostConverter postConverter,
		MemberGiver memberGiver, PostImageService postImageService, CommentGiver commentGiver,
		PostLikeService postLikeService) {
		this.postRepository = postRepository;
		this.postConverter = postConverter;
		this.memberGiver = memberGiver;
		this.postImageService = postImageService;
		this.commentGiver = commentGiver;
		this.postLikeService = postLikeService;
	}

	@Transactional
	public PostResponse.CreateResponse create(Long memberId, String content, List<MultipartFile> images) {
		Member member = postConverter.toMember(
			memberGiver.findById(memberId)
		);

		Post savePost = postRepository.save(
			Post.builder()
				.content(content)
				.member(member)
				.build()
		);

		postImageService.save(images, savePost);

		return new PostResponse.CreateResponse(savePost.getId(), memberId, content);
	}

	public List<PostResponse.FindAllResponse> findAll(Long memberId) {
		List<Member> members = memberGiver.findAllFollowing(memberId).stream()
			.map(postConverter::toMember)
			.toList();

		return postRepository.findByMemberIn(members).stream()
			.map(post ->
				postConverter.toDetailResponse(
					memberGiver.findById(members.iterator().next().getId()),
					post,
					postImageService.findByPostId(post.getId()),
					commentGiver.findByPostId(post.getId()),
					postLikeService.findByPostId(post.getId()),
					postLikeService.countByPostId(post.getId())
				)
			)
			.toList();
	}

	@Transactional
	public PostLikeResponse like(Long postId, Long memberId) {
		return postRepository.findById(postId)
			.map(post -> {
				MemberResponse memberResponse = memberGiver.findById(memberId);
				PostResponse postResponse = postConverter.toResponse(post);

				return postLikeService.like(postResponse, memberResponse);
			})
			.orElseThrow(() -> new NotFoundException("존재하지 않는 게시글입니다."));
	}

	@Transactional
	public PostLikeResponse unlike(Long postId, Long memberId) {
		return postRepository.findById(postId)
			.map(post -> {
				MemberResponse memberResponse = memberGiver.findById(memberId);
				PostResponse postResponse = postConverter.toResponse(post);

				return postLikeService.unlike(postResponse, memberResponse);
			})
			.orElseThrow(() -> new NotFoundException("존재하지 않는 게시글입니다."));
	}
}
