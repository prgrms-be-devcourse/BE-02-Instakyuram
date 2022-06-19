package com.kdt.instakyuram.common;

import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;

import javax.validation.constraints.Positive;

import org.hibernate.validator.constraints.Range;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import lombok.Builder;

public class PageDto {

	private PageDto() {
	}

	public record Request(@Positive(message = "자연수이여야 합니다.") Integer page,
						  @Range(min = 5, max = 10, message = "목록 단위는 5 ~ 10까지 가능합니다.") Integer size) {
		@Builder
		public Request(Integer page, Integer size) {
			if ((page == null) || (size == null || size < 5)) {
				page = 1;
				size = 10;
			}

			this.page = page;
			this.size = size;
		}

		public Pageable getPageable(Sort sort) {
			return PageRequest.of(page - 1, size, sort);
		}
	}

	public static class Response<DTO, DOMAIN> {
		private List<DTO> responses;
		private int totalPage;
		private int page;
		private int size;
		private int start;
		private int end;
		private boolean isPrevious;
		private boolean isNext;
		private List<Integer> pageNumbers;

		public Response(Page<DOMAIN> result, Function<DOMAIN, DTO> toResponse) {
			responses = result.stream().map(toResponse).toList();
			totalPage = result.getTotalPages();

			this.page = result.getPageable().getPageNumber() + 1;
			this.size = result.getPageable().getPageSize();

			//temp end page
			int tempEnd = (int)(Math.ceil(page / 10.0)) * 10;

			start = tempEnd - 9;

			isPrevious = start > 1;

			end = Math.min(totalPage, tempEnd);

			isNext = totalPage > tempEnd;

			pageNumbers = IntStream.rangeClosed(start, end).boxed().toList();

		}

		public List<DTO> getResponses() {
			return responses;
		}

		public int getTotalPage() {
			return totalPage;
		}

		public int getPage() {
			return page;
		}

		public int getSize() {
			return size;
		}

		public int getStart() {
			return start;
		}

		public int getEnd() {
			return end;
		}

		public boolean isPrevious() {
			return isPrevious;
		}

		public boolean isNext() {
			return isNext;
		}

		public List<Integer> getPageNumbers() {
			return pageNumbers;
		}
	}
}
