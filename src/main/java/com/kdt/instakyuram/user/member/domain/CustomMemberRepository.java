package com.kdt.instakyuram.user.member.domain;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.kdt.instakyuram.user.member.dto.MemberSearchDto;

public interface CustomMemberRepository {
	Page<Member> findByAllForPaging(Long auId, MemberSearchDto memberSearchDto, Pageable pageable);
}
