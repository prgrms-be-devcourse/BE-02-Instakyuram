package com.kdt.instakyuram.article.comment.domain;

import java.util.List;

import com.kdt.instakyuram.article.comment.dto.CommentFindAllResponse;

public interface CommentRepositoryCustom {

	List<CommentFindAllResponse> findAllByPostIdAndMemberId(Long postId, Long memberId, Long id, Integer limit);
}
