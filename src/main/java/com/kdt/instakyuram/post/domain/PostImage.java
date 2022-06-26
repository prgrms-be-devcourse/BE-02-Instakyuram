package com.kdt.instakyuram.post.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import com.kdt.instakyuram.common.BaseEntity;
import com.kdt.instakyuram.util.FileType;

import lombok.Builder;

@Entity
public class PostImage extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "post_id", referencedColumnName = "id")
	private Post post;

	@NotBlank
	private String originalFileName;

	@NotBlank
	private String serverFileName;

	@NotNull
	@Positive(message = "사이즈는 0이상이어야 합니다.")
	private Long size;

	@NotBlank(message = "파일 경로는 필수입니다.")
	private String path;

	protected PostImage() {
	}

	@Builder
	public PostImage(Long id, Post post, String originalFileName, String serverFileName, Long size, String path) {
		FileType.verifyType(getExtension(serverFileName));

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

	public String getFullPath() {
		return this.path + "/" + this.getServerFileName();
	}

	private String getExtension(String serverFileName) {
		return serverFileName.substring(
			serverFileName.lastIndexOf(".") + 1
		);
	}

}
