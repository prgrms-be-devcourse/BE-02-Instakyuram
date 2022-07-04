package com.kdt.instakyuram.article.comment.dto;

import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;

import lombok.Getter;

@Getter
public class CommentSearch {
	private Long id;
	private Integer limit;

	public CommentSearch(Long id, Integer limit) {
		this.id = defaultIfNull(id, 0L);
		this.limit = defaultIfNull(limit, 10);
	}
}
