package com.kdt.instakyuram.post.service;

import java.util.List;

import com.kdt.instakyuram.post.dto.PostImageResponse;
import com.kdt.instakyuram.post.dto.PostResponse;

public interface PostGiver {

	List<PostImageResponse.ThumbnailResponse> findPostThumbnailsByMemberId(Long memberId);

	List<PostImageResponse.ThumbnailResponse> findPostThumbnailsByUsername(String username);

	List<PostResponse> findAllByMemberId(Long memberId);
}