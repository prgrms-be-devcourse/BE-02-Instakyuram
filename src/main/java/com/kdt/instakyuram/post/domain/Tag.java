package com.kdt.instakyuram.post.domain;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.kdt.instakyuram.common.BaseEntity;

@Entity
public class Tag extends BaseEntity {
	@Id
	private Long id;
	private String name;
	private Long count;
}
