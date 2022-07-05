package com.kdt.instakyuram.user.member.dto;

import java.util.Objects;
import java.util.Optional;

import javax.validation.constraints.AssertFalse;

import org.springframework.data.domain.Sort;

public record MemberOrderDto(SortCondition sortCondition, Ordering ordering) {

	private static final String DEFAULT_SORT_COLUMN = "id";

	// note : @AssertFalse, @AssertTrue 로 validation을 할 때, is라는 네이밍을 붙이지 않으면 전파가 되지 않는다.
	@AssertFalse(message = "정렬조건을 입력해주세요.")
	public boolean isProperInputConstraint() {
		return Objects.isNull(sortCondition) && Objects.nonNull(ordering);
	}

	public Sort getSortingCriteria() {
		return Optional.ofNullable(this.sortCondition())
			.map(sortCondition -> Sort.by(sortCondition.getValue()))
			.orElseGet(() -> Sort.by(DEFAULT_SORT_COLUMN));
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

