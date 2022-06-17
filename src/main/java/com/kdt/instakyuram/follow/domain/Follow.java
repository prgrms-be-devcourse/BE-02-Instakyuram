package com.kdt.instakyuram.follow.domain;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Follow {
	@Id
	private Long id;

	private Long memberId;

	private Long targetId;

}
