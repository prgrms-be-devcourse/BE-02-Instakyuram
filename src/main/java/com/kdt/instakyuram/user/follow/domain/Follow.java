package com.kdt.instakyuram.user.follow.domain;

import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Positive;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.kdt.instakyuram.common.BaseEntity;
import com.kdt.instakyuram.exception.DomainException;

import lombok.Builder;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(
	name = "following", columnNames = {"memberId", "targetId"}
))
public class Follow extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Positive
	private Long memberId;

	@Positive
	private Long targetId;

	protected Follow() {
	}

	@Builder
	public Follow(Long id, Long memberId, Long targetId) {
		if (Objects.isNull(memberId) || Objects.isNull(targetId)) {
			throw new DomainException("팔로우 하는 대상과 팔로우 받는 대상은 반드시 필요합니다.");
		}

		checkFollow(memberId, targetId);

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

	public void checkFollow(Long memberId, Long targetId) {
		if (memberId.equals(targetId)) {
			throw new DomainException("자신을 팔로우 할 수는 없습니다.");
		}
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
