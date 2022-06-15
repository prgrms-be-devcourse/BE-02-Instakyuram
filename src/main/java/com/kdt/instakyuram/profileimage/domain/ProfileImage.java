package com.kdt.instakyuram.profileimage.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.kdt.instakyuram.member.domain.Member;

@Entity
public class ProfileImage {
	@Id
	private Long id;

	@ManyToOne
	@JoinColumn(name = "user_id", referencedColumnName = "id")
	private Member user;

	private String originalFileName;

	private String serverFileName;

	private Long size;

	private String path;
}
