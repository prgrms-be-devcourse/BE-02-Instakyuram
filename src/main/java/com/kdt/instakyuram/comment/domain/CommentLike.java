package com.kdt.instakyuram.comment.domain;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.kdt.instakyuram.comment.domain.Comment;
import com.kdt.instakyuram.member.domain.Member;

@Entity
public class CommentLike {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@JoinColumn(name = "comment_id")
	@ManyToOne(fetch = FetchType.LAZY)
	private Comment comment;

	@JoinColumn(name = "member_id")
	@ManyToOne(fetch = FetchType.LAZY)
	private Member member;

	protected CommentLike() {/*no-op*/}

	public CommentLike(Long id, Comment comment, Member member) {
		this.id = id;
		this.comment = comment;
		this.member = member;
	}

	public Long getId() {
		return id;
	}

	public Comment getComment() {
		return comment;
	}

	public Member getMember() {
		return member;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
			.append("id", id)
			.append("comment", comment)
			.toString();
	}
}
