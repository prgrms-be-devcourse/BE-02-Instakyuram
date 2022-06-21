package com.kdt.instakyuram.profileimage.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.kdt.instakyuram.common.BaseEntity;
import com.kdt.instakyuram.member.domain.Member;

@Entity
public class ProfileImage extends BaseEntity {
	@Id
	private Long id;

	@ManyToOne
	@JoinColumn(name = "user_id", referencedColumnName = "id")
	private Member user;

	private String originalFileName;

	private String serverFileName;

	private Long size;

	private String path;

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
			.append("id", id)
			.append("user", user)
			.append("originalFileName", originalFileName)
			.append("serverFileName", serverFileName)
			.append("size", size)
			.append("path", path)
			.toString();
	}
}
