package com.kdt.instakyuram.article.post.domain;

import java.util.List;

import com.kdt.instakyuram.user.member.domain.Member;

public interface PostRepositoryCustom {

	List<Post> search(List<Member> members, PostCondition postCondition);

}
