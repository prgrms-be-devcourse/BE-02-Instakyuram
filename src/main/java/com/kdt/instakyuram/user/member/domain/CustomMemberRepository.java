package com.kdt.instakyuram.user.member.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomMemberRepository {
	Page<Member> findAllExcludeAuth(Long auId, Pageable pageable);
}
