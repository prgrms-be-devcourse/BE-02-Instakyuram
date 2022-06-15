package com.kdt.instakyuram.comment.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.kdt.instakyuram.member.domain.Member;
import com.kdt.instakyuram.post.domain.Post;

@Entity
public class Comment {
	@Id
	private Long id;

	private String content;

	@JoinColumn(name = "post_id", referencedColumnName = "id")
	@ManyToOne
	private Post post;

	@JoinColumn(name = "user_id", referencedColumnName = "id")
	@ManyToOne
	private Member user;
}
