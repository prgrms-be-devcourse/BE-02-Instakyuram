package com.kdt.instakyuram.post.domain;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Tag {
	@Id
	private Long id;
	private String name;
	private Long tagCount;
}
