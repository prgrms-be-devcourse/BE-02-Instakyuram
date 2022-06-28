package com.kdt.instakyuram.article.post.service;

import java.util.List;

import com.kdt.instakyuram.article.postimage.dto.PostImageResponse;
import com.kdt.instakyuram.article.post.dto.PostResponse;

public interface PostGiver {

	List<PostImageResponse.ThumbnailResponse> findPostThumbnailsByMemberId(Long memberId);

	List<PostImageResponse.ThumbnailResponse> findPostThumbnailsByUsername(String username);

	List<PostResponse> findAllByMemberId(Long memberId);
}