package com.kdt.instakyuram.post.service;

import java.util.List;

import com.kdt.instakyuram.post.dto.PostImageResponse;

public interface PostGiver {

	List<PostImageResponse.ThumbnailResponse> findPostThumbnailsByMemberId(Long memberId);

}