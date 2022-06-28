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

import com.kdt.instakyuram.common.BaseEntity;
import com.kdt.instakyuram.user.member.domain.Member;

@Entity
public class CommentLike extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@JoinColumn(name = "comment_id")
	@ManyToOne(fetch = FetchType.LAZY)
	private Comment comment;

	@JoinColumn(name = "member_id")
	@ManyToOne(fetch = FetchType.LAZY)
	private Member member;

	protected CommentLike() {/*no-op*/}

	public CommentLike(Comment comment, Member member) {
		this(null, comment, member);
	}

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
