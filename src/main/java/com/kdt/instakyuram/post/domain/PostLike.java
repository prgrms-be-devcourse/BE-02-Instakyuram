package com.kdt.instakyuram.post.domain;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.kdt.instakyuram.member.domain.Member;

import lombok.Builder;

@Entity
public class PostLike {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@JoinColumn(name = "post_id")
	@ManyToOne(fetch = FetchType.LAZY)
	private Post post;

	@JoinColumn(name = "member_id")
	@ManyToOne(fetch = FetchType.LAZY)
	private Member member;

	protected PostLike() {
	}

	@Builder
	public PostLike(Long id, Post post, Member member) {
		this.id = id;
		this.post = post;
		this.member = member;
	}

	public Long getId() {
		return id;
	}

	public Post getPost() {
		return post;
	}

	public Member getMember() {
		return member;
	}
}
