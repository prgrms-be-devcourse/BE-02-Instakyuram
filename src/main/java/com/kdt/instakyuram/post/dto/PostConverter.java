package com.kdt.instakyuram.post.dto;

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
			member.getPhoneNumber()
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

	private static String extractExt(String originalFileName) {
		return originalFileName.substring(
			originalFileName.lastIndexOf(".")
		);
	}

	public PostResponse.FindAllResponse toDetailResponse(MemberResponse memberResponse, Post post,
		List<PostImageResponse> postImageResponse, List<CommentResponse> commentResponse,
		int totalPostLike) {
		return PostResponse.FindAllResponse.builder()
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

	public PostLike toPostLike(PostResponse postResponse, MemberResponse memberResponse) {
		return PostLike.builder()
			.post(Post.builder()
				.id(postResponse.id())
				.build())
			.member(Member.builder()
				.id(memberResponse.id())
				.build())
			.build();
	}
}
