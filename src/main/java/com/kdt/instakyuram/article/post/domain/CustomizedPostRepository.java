package com.kdt.instakyuram.article.post.domain;

import java.util.List;

import com.kdt.instakyuram.common.PageDto;

public interface CustomizedPostRepository {
	List<Post> findAllByUsernameCursorPaging(String username, PageDto.PostCursorPageRequest pageRequest);
}
