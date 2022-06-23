package com.kdt.instakyuram.member.service;

import java.util.List;

import com.kdt.instakyuram.member.dto.MemberRequest;
import com.kdt.instakyuram.member.dto.MemberResponse;

public interface MemberGiver {

	MemberResponse findById(Long id);

	List<MemberResponse> findAllFollowing(Long id);

	MemberResponse.SigninResponse signin(String username, String password);

	MemberResponse.SignupResponse signup(MemberRequest.SignupRequest request);

	List<MemberResponse> findAllFollowingIncludeMe(Long id);
}
