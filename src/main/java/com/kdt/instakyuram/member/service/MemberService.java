package com.kdt.instakyuram.member.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.kdt.instakyuram.common.PageDto;
import com.kdt.instakyuram.exception.NotFoundException;
import com.kdt.instakyuram.follow.service.FollowService;
import com.kdt.instakyuram.member.domain.Member;
import com.kdt.instakyuram.member.domain.MemberRepository;
import com.kdt.instakyuram.member.dto.MemberConverter;
import com.kdt.instakyuram.member.dto.MemberRequest;
import com.kdt.instakyuram.member.dto.MemberResponse;
import com.kdt.instakyuram.security.Role;
import com.kdt.instakyuram.security.jwt.Jwt;
import com.kdt.instakyuram.token.service.TokenService;

// TODO : MemberGiver의 메서드가 필요합니다 !
@Service
public class MemberService implements MemberGiver {

	private final FollowService followService;
	private final MemberConverter memberConverter;
	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;
	private final TokenService tokenService;
	private final Jwt jwt;

	public MemberService(FollowService followService, MemberRepository memberRepository,
		PasswordEncoder passwordEncoder,
		MemberConverter memberConverter, TokenService tokenService, Jwt jwt) {
		this.followService = followService;
		this.memberRepository = memberRepository;
		this.passwordEncoder = passwordEncoder;
		this.memberConverter = memberConverter;
		this.tokenService = tokenService;
		this.jwt = jwt;
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

	// todo : 요청한 사용자의 정보는 빼야함! -> 테스트 코드 변경
	public PageDto.Response<MemberResponse.MemberListViewResponse, Member> findAll(Pageable requestPage) {
		Page<Member> pagingMembers = memberRepository.findAll(requestPage);

		if (pagingMembers.getContent().isEmpty()) {
			throw new NotFoundException("사용자 목록이 존재하지 않습니다.");
		}

		return memberConverter.toPageResponse(pagingMembers);
	}

	public List<MemberResponse> findAllFollowing(Long id) {
		List<Long> followingIds = followService.findByFollowingIds(id);

		return memberRepository.findByIdIn(followingIds).stream()
			.map(memberConverter::toMemberResponse)
			.toList();
	}

	public MemberResponse.SigninResponse signin(String username, String password) {
		Member foundMember = memberRepository.findByUsername(username)
			.orElseThrow(() -> new NotFoundException("유저 정보가 일치하지 않습니다."));
		if (!passwordEncoder.matches(password, foundMember.getPassword())) {
			throw new NotFoundException("유저 정보가 일치하지 않습니다.");
		}
		String[] roles = {String.valueOf(Role.MEMBER)};
		String accessToken = jwt.generateAccessToken(Jwt.Claims.from(foundMember.getId().toString(), roles));
		String refreshToken = jwt.generateRefreshToken();
		tokenService.save(refreshToken, foundMember.getId());

		return new MemberResponse.SigninResponse(foundMember.getId(), username, accessToken, refreshToken);
	}
}
