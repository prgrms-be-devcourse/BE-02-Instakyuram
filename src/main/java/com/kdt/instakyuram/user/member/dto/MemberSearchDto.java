package com.kdt.instakyuram.user.member.dto;

import java.util.Objects;

import javax.validation.constraints.AssertTrue;

public record MemberSearchDto(MemberCondition memberCondition, SearchOrder order) {
	// todo : memberCondition이 있으면 order도 반드시 존재해야 한다.
	@AssertTrue(message = "")
	public boolean constraint() {
		return (
			Objects.isNull(memberCondition) && Objects.isNull(order)
		) ||
			(
				Objects.nonNull(memberCondition) && Objects.isNull(order)
			);
	}

	public enum MemberCondition {
		USERNAME("username"), UPDATED_AT("updatedAt"), NAME("name");

		private final String value;

		MemberCondition(String column) {
			this.value = column;
		}

		public String getValue() {
			return value;
		}
	}

	public enum SearchOrder {
		ASC, DESC;
	}
}

