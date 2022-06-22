package com.kdt.instakyuram.comment.dto;

import org.springframework.stereotype.Component;

import com.kdt.instakyuram.comment.domain.Comment;
import com.kdt.instakyuram.comment.domain.CommentLike;
import com.kdt.instakyuram.member.domain.Member;
import com.kdt.instakyuram.member.dto.MemberResponse;
import com.kdt.instakyuram.post.domain.Post;

@Component
public class CommentConverter {

	public Comment toComment(MemberResponse memberResponse, Long postId, String content) {
		Member member = toMember(memberResponse);
		Post post = toPost(postId);
		return new Comment(content, post, member);
	}

	public CommentResponse toResponse(Comment comment) {
		MemberResponse memberResponse = toMemberResponse(comment.getMember());
		return new CommentResponse(
			comment.getId(),
			comment.getContent(),
			memberResponse
		);
	}

	public CommentLike toCommentLike(CommentResponse commentResponse, MemberResponse memberResponse) {
		Comment comment = new Comment(commentResponse.id());
		Member member = Member.builder()
			.id(memberResponse.id())
			.build();

		return new CommentLike(comment, member);
	}

	private Member toMember(MemberResponse memberResponse) {
		return Member.builder()
			.id(memberResponse.id())
			.build();
	}

	private MemberResponse toMemberResponse(Member member) {
		return new MemberResponse(
			member.getId(),
			member.getUsername(),
			member.getName(),
			member.getEmail(),
			member.getPhoneNumber(),
			member.getIntroduction()
		);
	}

	private Post toPost(Long postId) {
		return Post.builder()
			.id(postId)
			.build();
	}
}
