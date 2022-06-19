package com.kdt.instakyuram.member.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.kdt.instakyuram.common.PageDto;
import com.kdt.instakyuram.exception.NotFoundException;
import com.kdt.instakyuram.member.domain.Member;
import com.kdt.instakyuram.member.domain.MemberRepository;
import com.kdt.instakyuram.member.dto.MemberConverter;
import com.kdt.instakyuram.member.dto.MemberRequest;
import com.kdt.instakyuram.member.dto.MemberResponse;

// TODO : MemberGiver의 메서드가 필요합니다 !
@Service
public class MemberService {

	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;

	private final MemberConverter memberConverter;

	public MemberService(MemberRepository memberRepository, PasswordEncoder passwordEncoder,
		MemberConverter memberConverter) {
		this.memberRepository = memberRepository;
		this.passwordEncoder = passwordEncoder;
		this.memberConverter = memberConverter;
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

	public MemberResponse.SignupResponse signup(MemberRequest.SignupRequest request) {
		Member member = memberRepository.save(new Member(request.username(),
			passwordEncoder.encode(request.password()),
			request.name(),
			request.phoneNumber(),
			request.email())
		);

		return new MemberResponse.SignupResponse(member.getId(), member.getUsername());
	}

	public PageDto.Response<MemberResponse.ViewResponse, Member> findAll(Pageable requestPage) {
		Page<Member> pagingMembers = memberRepository.findAll(requestPage);

		return memberConverter.toPageResponse(pagingMembers);
	}
}

