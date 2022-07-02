package com.kdt.instakyuram.article.post.domain;

import java.time.LocalDateTime;

import javax.validation.constraints.AssertTrue;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PostPagingCursor {
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime updatedAt;
	private Long id;

	public PostPagingCursor() {
	}

	public PostPagingCursor(LocalDateTime updatedAt, Long id) {
		this.updatedAt = updatedAt;
		this.id = id;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public Long getId() {
		return id;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@AssertTrue
	public boolean isValidCursor() {
		log.error("{} {}", updatedAt, id);

		return updatedAt != null && id != null;
	}
}



