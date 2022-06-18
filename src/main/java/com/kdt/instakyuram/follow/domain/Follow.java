package com.kdt.instakyuram.follow.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Builder;

@Entity
public class Follow {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long memberId;

	private Long targetId;

	protected Follow() {
	}

	@Builder
	public Follow(Long id, Long memberId, Long targetId) {
		this.id = id;
		this.memberId = memberId;
		this.targetId = targetId;
	}

	public Long getId() {
		return id;
	}

	public Long getTargetId() {
		return targetId;
	}

	public Long getMemberId() {
		return memberId;
	}
}
