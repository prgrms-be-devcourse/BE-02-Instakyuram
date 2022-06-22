package com.kdt.instakyuram.post.service;

import java.util.List;

import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.kdt.instakyuram.comment.service.CommentGiver;
import com.kdt.instakyuram.exception.NotFoundException;
import com.kdt.instakyuram.member.domain.Member;
import com.kdt.instakyuram.member.service.MemberGiver;
import com.kdt.instakyuram.post.domain.Post;
import com.kdt.instakyuram.post.domain.PostImage;
import com.kdt.instakyuram.post.domain.PostRepository;
import com.kdt.instakyuram.post.dto.PostConverter;
import com.kdt.instakyuram.post.dto.PostImageResponse;
import com.kdt.instakyuram.post.dto.PostLikeResponse;
import com.kdt.instakyuram.post.dto.PostResponse;
import com.kdt.instakyuram.util.ImageManager;

@Service
@Transactional(readOnly = true)
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

		Post savedPost = postRepository.save(
			Post.builder()
				.content(content)
				.member(member)
				.build()
		);

		List<PostImage> postImages = postConverter.toPostImages(images, savedPost);
		postImageService.save(postImages);

		for (int i = 0; i < postImages.size(); i++) {
			PostImage postImage = postImages.get(i);
			ImageManager.upload(images.get(i), postImage.getServerFileName(), postImage.getPath());
		}

		return new PostResponse.CreateResponse(savedPost.getId(), memberId, content);
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
					postLikeService.countByPostId(post.getId())
				)
			)
			.toList();
	}

	@Transactional
	public PostResponse.UpdateResponse update(Long id, Long memberId, String content) {
		return postRepository.findByIdAndMemberId(id, memberId)
			.map(post -> {
				post.updateContent(content);

				return postConverter.toUpdateResponse(post);
			})
			.orElseThrow(() -> new NotFoundException("존재하지 않는 게시글입니다."));

	}

	@Transactional
	public PostLikeResponse like(Long postId, Long memberId) {
		return postRepository.findById(postId)
			.map(post -> {
				memberGiver.findById(memberId);

				return postLikeService.like(postId, memberId);
			})
			.orElseThrow(() -> new NotFoundException("존재하지 않는 게시글입니다."));
	}

	@Transactional
	public PostLikeResponse unlike(Long postId, Long memberId) {
		return postRepository.findById(postId)
			.map(post -> {
				PostResponse postResponse = postConverter.toResponse(post);

				return postLikeService.unlike(postResponse, memberId);
			})
			.orElseThrow(() -> new NotFoundException("존재하지 않는 게시글입니다."));
	}

	public FileSystemResource findImage(Long postId, String serverFileName) {
		return postRepository.findById(postId)
			.map(post -> postImageService.findByServerFileName(serverFileName))
			.orElseThrow(() -> new NotFoundException("존재하지 않는 게시글입니다."));
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
			.orElseThrow(() -> new NotFoundException("존재하지 않는 게시글입니다."));

		deletedImages.forEach(image -> {
			ImageManager.delete(image.path(), image.serverFileName());
		});

		return id;
	}
}
