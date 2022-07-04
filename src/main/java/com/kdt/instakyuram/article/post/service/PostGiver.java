package com.kdt.instakyuram.article.post.service;

import java.util.List;

import com.kdt.instakyuram.article.post.domain.PostPagingCursor;
import com.kdt.instakyuram.article.post.dto.PostResponse;
import com.kdt.instakyuram.article.postimage.dto.PostImageResponse;
import com.kdt.instakyuram.common.PageDto;

public interface PostGiver {

	List<PostImageResponse.ThumbnailResponse> findPostThumbnailsByMemberId(Long memberId);

	PageDto.CursorResponse<PostImageResponse.ThumbnailResponse, PostPagingCursor> findPostThumbnailsByUsername(
		String username,
		PageDto.PostCursorPageRequest pageRequest);

	List<PostResponse> findAllByMemberId(Long memberId);
}