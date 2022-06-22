package com.kdt.instakyuram.follow.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.kdt.instakyuram.common.BaseEntity;

import lombok.Builder;

@Entity
public class Follow extends BaseEntity {
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

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
			.append("id", id)
			.append("memberId", memberId)
			.append("targetId", targetId)
			.append("createdAt", super.getCreatedAt())
			.append("updatedAt", super.getUpdatedAt())
			.append("createdBy", super.getCreatedBy())
			.append("updatedBy", super.getUpdatedBy())
			.toString();
	}
}
