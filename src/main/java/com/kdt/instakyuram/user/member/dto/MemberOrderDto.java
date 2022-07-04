package com.kdt.instakyuram.user.member.dto;

import java.util.Objects;

import javax.validation.constraints.AssertFalse;

public record MemberOrderDto(SortCondition sortCondition, Ordering ordering) {

	// note : @AssertFalse, @AssertTrue 로 validation을 할 때, is라는 네이밍을 붙이지 않으면 전파가 되지 않는다.
	@AssertFalse(message = "정렬조건을 입력해주세요.")
	public boolean isProper() {
		return Objects.isNull(sortCondition) && Objects.nonNull(ordering);
	}

	public boolean isExistSort() {
		return this.sortCondition != null;
	}

	public enum SortCondition {
		USERNAME("username"), UPDATED_AT("updatedAt"), NAME("name");

		private final String value;

		SortCondition(String column) {
			this.value = column;
		}

		public String getValue() {
			return value;
		}
	}

	public enum Ordering {
		ASC, DESC;

		public boolean isAscending() {
			return this == ASC;
		}
	}

}

