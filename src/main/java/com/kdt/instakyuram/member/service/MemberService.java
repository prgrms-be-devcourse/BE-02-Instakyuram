package com.kdt.instakyuram.member.service;

import org.springframework.stereotype.Service;

import com.kdt.instakyuram.exception.NotFoundException;
import com.kdt.instakyuram.member.domain.Member;
import com.kdt.instakyuram.member.domain.MemberRepository;
import com.kdt.instakyuram.member.dto.MemberResponse;

// TODO : MemberGiver의 메서드가 필요합니다 !
@Service
public class MemberService {

	private final MemberRepository memberRepository;

	public MemberService(MemberRepository memberRepository) {
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
}
