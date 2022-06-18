package com.kdt.instakyuram.member.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.kdt.instakyuram.exception.NotFoundException;
import com.kdt.instakyuram.follow.service.FollowService;
import com.kdt.instakyuram.member.domain.Member;
import com.kdt.instakyuram.member.domain.MemberRepository;
import com.kdt.instakyuram.member.dto.MemberResponse;
import com.kdt.instakyuram.member.dto.MemberResponse.FollowingMember;

@Service
public class MemberService {

	private final FollowService followService;
	private final MemberRepository memberRepository;

	public MemberService(FollowService followService, MemberRepository memberRepository) {
		this.followService = followService;
		this.memberRepository = memberRepository;
	}

	public MemberResponse findById(Long id) {
		Member foundMember = memberRepository.findById(id)
			.orElseThrow(() -> new NotFoundException("유저 정보가 존재하지 않습니다."));

		return new MemberResponse(
			foundMember.getId(),
			foundMember.getUsername(),
			foundMember.getName(),
			foundMember.getEmail(),
			foundMember.getPhoneNumber()
		);
	}

	public List<FollowingMember> findFollowingMembers(Long id) {
		List<Long> followingIds = followService.findByFollowingIds(id);

		return memberRepository.findByIdIn(followingIds).stream()
			.map(followingMember -> new FollowingMember(
				followingMember.getId(),
				followingMember.getUsername(),
				followingMember.getName())
			).toList();
	}
}
