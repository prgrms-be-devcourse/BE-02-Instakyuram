package com.kdt.instakyuram.article.post.service;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.kdt.instakyuram.article.comment.dto.CommentResponse;
import com.kdt.instakyuram.article.comment.service.CommentGiver;
import com.kdt.instakyuram.common.file.exception.FileWriteException;
import com.kdt.instakyuram.exception.EntityNotFoundException;
import com.kdt.instakyuram.exception.ErrorCode;
import com.kdt.instakyuram.article.postimage.service.PostImageService;
import com.kdt.instakyuram.user.member.domain.Member;
import com.kdt.instakyuram.user.member.service.MemberGiver;
import com.kdt.instakyuram.article.post.domain.Post;
import com.kdt.instakyuram.article.post.domain.PostRepository;
import com.kdt.instakyuram.article.post.dto.PostConverter;
import com.kdt.instakyuram.article.postimage.dto.PostImageResponse;
import com.kdt.instakyuram.article.post.dto.PostLikeResponse;
import com.kdt.instakyuram.article.post.dto.PostResponse;
import com.kdt.instakyuram.util.ImageManager;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true)
public class PostService implements PostGiver {

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

	@Transactional(rollbackFor = {FileWriteException.class})
	public PostResponse.CreateResponse create(Long memberId, String content, List<MultipartFile> images) {
		Member member = postConverter.toMember(memberGiver.findById(memberId));
		Post savedPost = postRepository.save(
			Post.builder()
				.content(content)
				.member(member)
				.build()
		);
		postImageService.save(savedPost.getId(), images);

		return new PostResponse.CreateResponse(savedPost.getId(), memberId, content);
	}

	public List<PostResponse.FindAllResponse> findAllRelated(Long memberId) {
		List<Member> members = memberGiver.findAllFollowingIncludeMe(memberId).stream()
			.map(postConverter::toMember)
			.toList();
		List<Post> posts = postRepository.findAllByMemberIn(members);

		Map<Long, List<PostImageResponse>> postImages = postImageService.findByPostIn(posts)
			.stream()
			.collect(Collectors.groupingBy(PostImageResponse::postId));
		Map<Long, List<CommentResponse>> comments = commentGiver.findByPostIn(posts)
			.stream()
			.collect(Collectors.groupingBy(CommentResponse::postId));
		Map<Long, Long> postLikes = postLikeService.findByPostIn(posts)
			.stream()
			.collect(Collectors.groupingBy(PostLikeResponse::postId, Collectors.counting()));

		return posts.stream()
			.map(post -> {
				var member = postConverter.toMemberResponse(post.getMember());
				var postImageResponses = postImages.get(post.getId());
				var commentResponses = comments.get(post.getId());
				var totalPostLike = postLikes.getOrDefault(post.getId(), 0L).intValue();

				return postConverter.toDetailResponse(
					member,
					post,
					postImageResponses,
					commentResponses,
					totalPostLike
				);
			}).toList();
	}

	@Transactional
	public PostResponse.UpdateResponse update(Long id, Long memberId, String content) {
		return postRepository.findByIdAndMemberId(id, memberId)
			.map(post -> {
				post.updateContent(content);

				return postConverter.toUpdateResponse(post);
			})
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.POST_NOT_FOUND,
				MessageFormat.format("Post ID = {0}, Member ID = {1}, Content = {2}", id, memberId, content)));

	}

	// 비관적 락 적용
	@Transactional
	public PostResponse.UpdateResponse lockedUpdate(Long id, Long memberId, String content) {
		return postRepository.findByIdAndMemberId_Locked_Pessimistic(id, memberId)
			.map(post -> {
				post.updateContent(content);

				return postConverter.toUpdateResponse(post);
			})
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.POST_NOT_FOUND,
				MessageFormat.format("Post ID = {0}, Member ID = {1}, Content = {2}", id, memberId, content)));
	}

	@Transactional
	public PostLikeResponse like(Long postId, Long memberId) {
		return postRepository.findById(postId)
			.map(post -> {
				memberGiver.findById(memberId);

				return postLikeService.like(postId, memberId);
			})
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.POST_NOT_FOUND,
				MessageFormat.format("Post ID = {0}, Member ID = {1}", postId, memberId)));
	}

	@Transactional
	public PostLikeResponse unlike(Long postId, Long memberId) {
		return postRepository.findById(postId)
			.map(post -> {
				PostResponse postResponse = postConverter.toResponse(post);

				return postLikeService.unlike(postResponse, memberId);
			})
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.POST_NOT_FOUND,
				MessageFormat.format("Post ID = {0}, Member ID = {1}", postId, memberId)));
	}

	public FileSystemResource findImage(Long postId, String serverFileName) {
		return postRepository.findById(postId)
			.map(post -> postImageService.findByServerFileName(serverFileName))
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.POST_NOT_FOUND,
				MessageFormat.format("Post ID = {0}, ServerFileName = {1}", postId, serverFileName)));
	}

	@Transactional
	public Long delete(Long id, Long memberId) {
		List<PostImageResponse.DeleteResponse> deletedImages = postRepository.findByIdAndMemberId(id, memberId)
			.map(post -> {
				//TODO : commentGiver.delete(id);
				postLikeService.delete(id);
				List<PostImageResponse.DeleteResponse> images = postImageService.delete(id);
				postRepository.delete(post);

				return images;
			})
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.POST_NOT_FOUND,
				MessageFormat.format("Post ID = {0}, Member ID = {1}", id, memberId)));

		deletedImages.forEach(image ->
			ImageManager.delete(image.path(), image.serverFileName()));

		return id;
	}

	@Override
	public List<PostImageResponse.ThumbnailResponse> findPostThumbnailsByMemberId(Long memberId) {
		List<Post> posts = postRepository.findAllByMemberId(memberId);

		if (posts.isEmpty()) {
			return Collections.emptyList();
		}

		return posts.stream()
			.map(Post::getId)
			.map(postImageService::findThumbnailByPostId)
			.toList();
	}

	@Override
	public List<PostImageResponse.ThumbnailResponse> findPostThumbnailsByUsername(String username) {
		List<Post> posts = postRepository.findAllByUsername(username);

		if (posts.isEmpty()) {
			return Collections.emptyList();
		}

		return posts.stream()
			.map(Post::getId)
			.map(postImageService::findThumbnailByPostId)
			.toList();
	}

	public List<PostResponse> findAllByMemberId(Long memberId) {
		return postRepository.findAllByMemberId(memberId)
			.stream()
			.map(postConverter::toResponse)
			.toList();
	}
}
