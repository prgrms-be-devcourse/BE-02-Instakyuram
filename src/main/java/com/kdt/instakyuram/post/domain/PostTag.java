package com.kdt.instakyuram.post.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class PostTag {
	@Id
	private Long id;

	@JoinColumn(name = "tag_id", referencedColumnName = "id")
	@ManyToOne
	private Tag tag;

	@JoinColumn(name = "post_id", referencedColumnName = "id")
	@ManyToOne
	private Post post;
}
