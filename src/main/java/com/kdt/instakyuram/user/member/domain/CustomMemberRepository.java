package com.kdt.instakyuram.user.member.domain;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.kdt.instakyuram.user.member.dto.MemberOrderDto;

public interface CustomMemberRepository {
	Page<Member> findAllExcludeAuth(Long auId, MemberOrderDto memberOrderDto, Pageable pageable);
}
