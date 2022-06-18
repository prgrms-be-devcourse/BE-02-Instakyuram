package com.kdt.instakyuram.member.service;

import java.util.List;

import com.kdt.instakyuram.member.dto.MemberResponse;

public interface MemberGiver {

	MemberResponse findById(Long id);

	List<MemberResponse> findAllFollowing(Long id);
}
