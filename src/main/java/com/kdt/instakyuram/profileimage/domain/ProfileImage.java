package com.kdt.instakyuram.profileimage.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.validator.constraints.Range;

import com.kdt.instakyuram.common.BaseEntity;
import com.kdt.instakyuram.member.domain.Member;

import lombok.Builder;

@Entity
public class ProfileImage extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private Member member;

	@NotBlank
	@Column(nullable = false)
	private String originalFileName;

	@NotBlank
	@Column(nullable = false)
	private String serverFileName;

	@NotNull
	@Range(max = 10485760)
	@Column(nullable = false)
	private Long size;

	@NotBlank
	@Column(nullable = false)
	private String path;

	protected ProfileImage() {

	}

	@Builder
	public ProfileImage(Long id, Member member, String originalFileName, String serverFileName, Long size,
		String path) {
		this.id = id;
		this.member = member;
		this.originalFileName = originalFileName;
		this.serverFileName = serverFileName;
		this.size = size;
		this.path = path;
	}

	public Long getId() {
		return id;
	}

	public Member getMember() {
		return member;
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

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
			.append("id", id)
			.append("user", member)
			.append("originalFileName", originalFileName)
			.append("serverFileName", serverFileName)
			.append("size", size)
			.append("path", path)
			.toString();
	}
}
