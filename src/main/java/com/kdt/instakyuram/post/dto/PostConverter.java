package com.kdt.instakyuram.post.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.kdt.instakyuram.comment.dto.CommentResponse;
import com.kdt.instakyuram.member.domain.Member;
import com.kdt.instakyuram.member.dto.MemberResponse;
import com.kdt.instakyuram.post.domain.Post;
import com.kdt.instakyuram.post.domain.PostImage;
import com.kdt.instakyuram.post.domain.PostLike;

@Component
public class PostConverter {

	// 프로젝트 최상단의 picture에 이미지가 저장됩니다.
	private final String path = System.getProperty("user.dir") + "/picture/";

	public Member toMember(MemberResponse memberResponse) {
		return Member.builder()
			.id(memberResponse.id())
			.build();
	}

	public MemberResponse toMemberResponse(Member member) {
		return new MemberResponse(
			member.getId(),
			member.getUsername(),
			member.getName(),
			member.getEmail(),
			member.getPhoneNumber(),
			member.getIntroduction()
		);
	}

	public PostImage toPostImage(MultipartFile file, Post post) {
		String originalFileName = file.getOriginalFilename();
		String serverFileName = UUID.randomUUID() + extractExt(originalFileName);

		return PostImage.builder()
			.post(post)
			.originalFileName(originalFileName)
			.serverFileName(serverFileName)
			.path(path)
			.size(file.getSize())
			.build();
	}

	public PostImageResponse.ThumbnailResponse toThumbnailResponse(Long postId, PostImage postImage) {
		return new PostImageResponse.ThumbnailResponse(
			postId,
			postImage.getServerFileName(),
			postImage.getPath(),
			postImage.getSize()
		);
	}

	public List<PostImage> toPostImages(List<MultipartFile> files, Post post) {
		List<PostImage> postImages = new ArrayList<>();
		for (MultipartFile file : files) {
			String originalFileName = file.getOriginalFilename();
			String serverFileName = UUID.randomUUID() + extractExt(originalFileName);

			postImages.add(
				PostImage.builder()
					.post(post)
					.originalFileName(originalFileName)
					.serverFileName(serverFileName)
					.path(path)
					.size(file.getSize())
					.build());
		}

		return postImages;
	}

	private static String extractExt(String originalFileName) {
		return originalFileName.substring(
			originalFileName.lastIndexOf(".")
		);
	}

	public PostResponse.FindAllResponse toDetailResponse(MemberResponse memberResponse, Post post,
		List<PostImageResponse> postImageResponse, List<CommentResponse> commentResponse,
		int totalPostLike) {
		return PostResponse.FindAllResponse.builder()
			.id(post.getId())
			.content(post.getContent())
			.member(memberResponse)
			.postImageResponse(postImageResponse)
			.commentResponse(commentResponse)
			.totalPostLike(totalPostLike)
			.build();

	}

	public PostResponse toResponse(Post post) {
		return PostResponse.builder()
			.id(post.getId())
			.content(post.getContent())
			.memberResponse(toMemberResponse(post.getMember()))
			.build();
	}

	public PostImageResponse toPostImageResponse(PostImage postImage) {
		return PostImageResponse.builder()
			.id(postImage.getId())
			.path(postImage.getPath())
			.serverFileName(postImage.getServerFileName())
			.originalFileName(postImage.getOriginalFileName())
			.size(postImage.getSize())
			.build();
	}

	public PostLike toPostLike(Long postId, Long memberId) {
		Post post = Post.builder()
			.id(postId)
			.build();
		Member member = Member.builder()
			.id(memberId)
			.build();

		return PostLike.builder()
			.post(post)
			.member(member)
			.build();
	}

	public PostResponse.UpdateResponse toUpdateResponse(Post post) {
		return new PostResponse.UpdateResponse(post.getId(), post.getContent());
	}

	public PostImageResponse.DeleteResponse toDeletePostImageResponse(PostImage postImage) {
		return new PostImageResponse.DeleteResponse(postImage.getServerFileName(), postImage.getPath());
	}
}
