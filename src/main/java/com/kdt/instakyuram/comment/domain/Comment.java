package com.kdt.instakyuram.comment.domain;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.kdt.instakyuram.common.BaseEntity;
import com.kdt.instakyuram.user.member.domain.Member;
import com.kdt.instakyuram.post.domain.Post;

@Entity
public class Comment extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank
	@NotNull
	private String content;

	@JoinColumn(name = "post_id")
	@ManyToOne(fetch = FetchType.LAZY)
	private Post post;

	@JoinColumn(name = "member_id")
	@ManyToOne(fetch = FetchType.LAZY)
	private Member member;

	protected Comment() {/*no-op*/}

	public Comment(Long id) {
		this(id, null, null, null);
	}

	public Comment(String content, Post post, Member member) {
		this(null, content, post, member);
	}

	public Comment(Long id, String content, Post post, Member member) {
		this.id = id;
		this.content = content;
		this.post = post;
		this.member = member;
	}

	public Long getId() {
		return id;
	}

	public String getContent() {
		return content;
	}

	public Post getPost() {
		return post;
	}

	public Member getMember() {
		return member;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
			.append("id", id)
			.append("content", content)
			.toString();
	}
}
