package com.kdt.instakyuram.post.dto;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.kdt.instakyuram.member.domain.Member;
import com.kdt.instakyuram.member.dto.MemberResponse;
import com.kdt.instakyuram.post.domain.Post;
import com.kdt.instakyuram.post.domain.PostImage;

@Component
public class PostConverter {

	@Value("${file.dir.post}")
	private String path;

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
		String serverFileName = originalFileName + extractExt(originalFileName);

		return PostImage.builder()
			.post(post)
			.originalFileName(originalFileName)
			.serverFileName(serverFileName)
			.path(path)
			.size(file.getSize())
			.build();
	}

	public PostResponse.FindAllResponse toResponse(Post post) {
		return new PostResponse.FindAllResponse(post.getContent(), toMemberResponse(post.getMember()));
	}

	private static String extractExt(String originalFileName) {
		return originalFileName.substring(
			originalFileName.lastIndexOf(".")
		);
	}

}
