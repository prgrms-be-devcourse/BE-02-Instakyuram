package com.kdt.instakyuram.user.member.service;

import java.util.List;

import com.kdt.instakyuram.user.member.dto.MemberRequest;
import com.kdt.instakyuram.user.member.dto.MemberResponse;

public interface MemberGiver {

	MemberResponse findById(Long id);

	List<MemberResponse> findAllFollowing(Long id);

	List<MemberResponse> findAllFollowingIncludeMe(Long id);

	MemberResponse.SigninResponse signin(String username, String password);

	MemberResponse.SignupResponse signup(MemberRequest.SignupRequest request);
}