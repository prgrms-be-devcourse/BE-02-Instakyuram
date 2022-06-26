package com.kdt.instakyuram.comment.domain;

import java.util.List;

import com.kdt.instakyuram.comment.dto.CommentFindAllResponse;

public interface CommentRepositoryCustom {

	List<CommentFindAllResponse> findAllByPostIdAndMemberId(Long postId, Long memberId);
}
