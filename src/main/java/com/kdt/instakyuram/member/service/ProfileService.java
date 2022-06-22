package com.kdt.instakyuram.member.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kdt.instakyuram.follow.service.FollowService;
import com.kdt.instakyuram.member.dto.MemberResponse;
import com.kdt.instakyuram.post.service.PostService;

@Service
@Transactional(readOnly = true)
public class ProfileService {

	private final MemberService memberService;
	private final PostService postService;
	private final FollowService followService;

	public ProfileService(MemberService memberService, PostService postService, FollowService followService) {
		this.memberService = memberService;
		this.postService = postService;
		this.followService = followService;
	}

	public MemberResponse.ProfileInfoResponse findProfileInfoByUsername(String username) {
		MemberResponse foundMember = memberService.findByUsername(username);

		return new MemberResponse.ProfileInfoResponse(
			foundMember.id(),
			foundMember.username(),
			foundMember.name(),
			foundMember.introduction(),
			(long)postService.findAllByMemberId(foundMember.id()).size(),
			followService.countMyFollower(foundMember.id()),
			followService.countMyFollowing(foundMember.id())
		);
	}
}

