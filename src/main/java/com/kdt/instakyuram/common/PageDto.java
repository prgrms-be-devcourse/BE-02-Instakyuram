package com.kdt.instakyuram.common;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import org.hibernate.validator.constraints.Range;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.kdt.instakyuram.article.post.domain.PostPagingCriteria;
import com.kdt.instakyuram.article.post.domain.PostPagingCursor;
import com.kdt.instakyuram.article.post.dto.PostResponse;

public class PageDto {

	private PageDto() {
	}

	public record Request(@NotNull(message = "숫자를 입력해주세요")
						  @Positive Integer page,
						  @NotNull(message = "숫자를 입력해주세요")
						  @Range(min = 5, max = 10, message = "목록 단위는 5 ~ 10까지 가능합니다.") Integer size) {
		public Pageable getPageable(Sort sort) {
			return PageRequest.of(page - 1, size, sort);
		}
	}

	public static class PostFindAllPageRequest {

		private PostPagingCriteria postPagingCriteria;
		@Positive
		private Integer size;

		public PostFindAllPageRequest() {
		}

		public PostPagingCriteria getPostPagingCriteria() {
			return postPagingCriteria;
		}

		public void setPostPagingCriteria(PostPagingCriteria postPagingCriteria) {
			this.postPagingCriteria = postPagingCriteria;
		}

		public Integer getSize() {
			return size;
		}

		public void setSize(Integer size) {
			this.size = size;
		}

	}

	public static class PostFindAllPageResponse {

		List<PostResponse.FindAllResponse> responses;
		boolean hasNext;
		Long lastId;
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
		LocalDateTime begin;
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
		LocalDateTime end;

		public PostFindAllPageResponse(
			List<PostResponse.FindAllResponse> responses, boolean hasNext, Long lastId, LocalDateTime begin,
			LocalDateTime end) {
			this.responses = responses;
			this.hasNext = hasNext;
			this.lastId = lastId;
			this.begin = begin;
			this.end = end;
		}

		public List<PostResponse.FindAllResponse> getResponses() {
			return responses;
		}

		public boolean isHasNext() {
			return hasNext;
		}

		public Long getLastId() {
			return lastId;
		}

		public LocalDateTime getBegin() {
			return begin;
		}

		public LocalDateTime getEnd() {
			return end;
		}
	}

	public static class PostCursorPageRequest {
		@Valid
		private PostPagingCursor cursor;
		private Integer size;

		public PostCursorPageRequest(PostPagingCursor cursor, Integer size) {
			this.cursor = cursor;
			this.size = size;
		}

		public PostPagingCursor getCursor() {
			return cursor;
		}

		public Integer getSize() {
			return size;
		}

		public void setCursor(PostPagingCursor cursor) {
			this.cursor = cursor;
		}

		public void setSize(Integer size) {
			this.size = size;
		}
	}

	public static class Response<DTO, DOMAIN> {
		private final List<DTO> responses;
		private final int totalPage;
		private final int page;
		private final int size;
		private final int start;
		private final int end;
		private final boolean hasPrevious;
		private final boolean hasNext;
		private final List<Integer> pageNumbers;

		public Response(Page<DOMAIN> result, Function<DOMAIN, DTO> toResponse) {
			responses = result.stream().map(toResponse).toList();
			totalPage = result.getTotalPages();

			this.page = result.getPageable().getPageNumber() + 1;
			this.size = result.getPageable().getPageSize();

			int tempEnd = (int)(Math.ceil(page / (double)size)) * size;

			start = tempEnd - (size - 1);

			hasPrevious = start > 1;

			end = Math.min(totalPage, tempEnd);

			hasNext = totalPage > tempEnd;

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

		public boolean hasPrevious() {
			return hasPrevious;
		}

		public boolean hasNext() {
			return hasNext;
		}

		public List<Integer> getPageNumbers() {
			return pageNumbers;
		}
	}

	public record CursorResponse<T, C>(List<T> values, Boolean hasNext, C cursor) {
	}
}
