package com.kdt.instakyuram.post.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.kdt.instakyuram.common.BaseEntity;

import lombok.Builder;

@Entity
public class PostImage extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "post_id", referencedColumnName = "id")
	private Post post;

	private String originalFileName;

	private String serverFileName;

	private Long size;

	private String path;

	protected PostImage() {
	}

	@Builder
	public PostImage(Long id, Post post, String originalFileName, String serverFileName, Long size, String path) {
		this.id = id;
		this.post = post;
		this.originalFileName = originalFileName;
		this.serverFileName = serverFileName;
		this.size = size;
		this.path = path;
	}

	public Long getId() {
		return id;
	}

	public Post getPost() {
		return post;
	}

	public String getOriginalFileName() {
		return originalFileName;
	}

	public String getServerFileName() {
		return serverFileName;
	}

	public Long getSize() {
		return size;
	}

	public String getPath() {
		return path;
	}

}
