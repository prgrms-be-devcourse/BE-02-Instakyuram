package com.kdt.instakyuram.article.post.domain;

import java.time.LocalDateTime;
import java.util.List;

import com.kdt.instakyuram.common.PageDto;
import com.kdt.instakyuram.user.member.domain.Member;

public interface CustomizedPostRepository {

	List<Post> findAllCursorPaging(List<Member> members, PageDto.PostFindAllPageRequest pageRequest);

	boolean isFindAllCursorHasNext(List<Member> members, Long id, LocalDateTime begin, LocalDateTime end);

}
