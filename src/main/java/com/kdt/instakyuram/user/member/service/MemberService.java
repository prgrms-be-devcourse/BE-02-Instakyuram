package com.kdt.instakyuram.user.member.service;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kdt.instakyuram.auth.service.TokenService;
import com.kdt.instakyuram.common.PageDto;
import com.kdt.instakyuram.exception.BusinessException;
import com.kdt.instakyuram.exception.EntityNotFoundException;
import com.kdt.instakyuram.exception.ErrorCode;
import com.kdt.instakyuram.security.Role;
import com.kdt.instakyuram.security.jwt.Jwt;
import com.kdt.instakyuram.user.follow.service.FollowService;
import com.kdt.instakyuram.user.member.domain.Member;
import com.kdt.instakyuram.user.member.domain.MemberRepository;
import com.kdt.instakyuram.user.member.dto.MemberConverter;
import com.kdt.instakyuram.user.member.dto.MemberRequest;
import com.kdt.instakyuram.user.member.dto.MemberResponse;

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
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.MEMBER_NOT_FOUND,
				MessageFormat.format("Member ID = {0}", id)));

		return new MemberResponse(
			foundMember.getId(),
			foundMember.getUsername(),
			foundMember.getName(),
			foundMember.getEmail(),
			foundMember.getPhoneNumber(),
			foundMember.getIntroduction(),
			foundMember.getProfileImageName()
		);
	}

	public MemberResponse findByUsername(String username) {
		Member foundMember = memberRepository.findByUsername(username)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.MEMBER_NOT_FOUND,
				MessageFormat.format("Username = {0}", username)));

		return new MemberResponse(
			foundMember.getId(),
			foundMember.getUsername(),
			foundMember.getName(),
			foundMember.getEmail(),
			foundMember.getPhoneNumber(),
			foundMember.getIntroduction(),
			foundMember.getProfileImageName()
		);
	}

	public MemberResponse.SignUpResponse signUp(MemberRequest.SignUpRequest request) {
		Member member = memberRepository.save(new Member(request.username(),
			passwordEncoder.encode(request.password()),
			request.name(),
			request.phoneNumber(),
			request.email(),
			"")
		);

		return new MemberResponse.SignUpResponse(member.getId(), member.getUsername());
	}

	public PageDto.Response<MemberResponse.MemberListViewResponse, Member> findAll(Long authId, Pageable requestPage) {
		Page<Member> pagingMembers = memberRepository.findAllExcludeAuth(authId, requestPage);

		if (pagingMembers.getContent().isEmpty()) {
			throw new EntityNotFoundException(ErrorCode.MEMBER_NOT_FOUND);
		}

		List<Long> memberIds = pagingMembers.getContent().stream().map(Member::getId).toList();
		Set<Long> authFollowings = followService.findAuthFollowings(authId, memberIds);

		return memberConverter.toPageResponse(pagingMembers, authFollowings);
	}

	public List<MemberResponse> findAllFollowing(Long id) {
		List<Long> followingIds = followService.findByFollowingIds(id);

		return memberRepository.findByIdIn(followingIds).stream()
			.map(memberConverter::toMemberResponse)
			.toList();
	}

	@Override
	public List<MemberResponse> findAllFollowingIncludeMe(Long id) {
		List<Long> ids = followService.findByFollowingIds(id);

		return memberRepository.findAllIdsInOrById(ids, id).stream()
			.map(memberConverter::toMemberResponse)
			.toList();
	}

	public MemberResponse.SignInResponse signIn(String username, String password) {
		Member foundMember = memberRepository.findByUsername(username)
			.orElseThrow(() -> new BusinessException(ErrorCode.AUTHENTICATION_FAILED,
				MessageFormat.format("Username = {0}, Password ={1}", username, password)));
		if (!passwordEncoder.matches(password, foundMember.getPassword())) {
			throw new BusinessException(ErrorCode.AUTHENTICATION_FAILED,
				MessageFormat.format("Password = {0}", password));
		}
		String[] roles = {String.valueOf(Role.MEMBER)};
		String accessToken = jwt.generateAccessToken(foundMember.getId(), foundMember.getUsername(), roles);
		String refreshToken = jwt.generateRefreshToken();
		tokenService.save(refreshToken, foundMember.getId());

		return new MemberResponse.SignInResponse(foundMember.getId(), username, accessToken, refreshToken, roles);
	}

	public Long countMyFollowing(Long memberId) {
		return followService.countMyFollowing(memberId);
	}

	public Long countMyFollower(Long memberId) {
		return followService.countMyFollower(memberId);
	}

	@Transactional
	public String updateProfileImage(Long id, String profileImageName) {
		return this.memberRepository.findById(id)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.MEMBER_NOT_FOUND,
				MessageFormat.format("Member ID = {0}", id)))
			.updateProfileImage(profileImageName);
	}

	public List<MemberResponse.FollowerResponse> getFollowers(Long authId, String username, Long lastIdx) {

		List<Long> myFollowerIds = followService.findByMyFollower(
			memberRepository.findByUsername(username)
				.map(Member::getId)
				.orElseThrow(
					() -> new EntityNotFoundException(ErrorCode.MEMBER_NOT_FOUND,
						MessageFormat.format("해당 username 이 {0}인 사용자가 존재하지 않습니다", username))),
			lastIdx
		);

		if (myFollowerIds.isEmpty()) {
			return Collections.emptyList();
		}

		Set<Long> authFollowings = followService.findAuthFollowings(authId, myFollowerIds);

		return memberRepository.findByIdInOrderById(myFollowerIds).stream()
			.map(member -> {
				boolean isMe = member.getId().equals(authId);

				if (authFollowings.contains(member.getId())) {
					return memberConverter.toFollower(member, true, isMe);
				}

				return memberConverter.toFollower(member, false, isMe);
			})
			.toList();
	}

	public List<MemberResponse.FollowingResponse> getFollowings(Long authId, String username, Long lastIdx) {

		List<Long> myFollowingIds = followService.findByMyFollowings(
			memberRepository.findByUsername(username)
				.map(Member::getId)
				.orElseThrow(
					() -> new EntityNotFoundException(ErrorCode.MEMBER_NOT_FOUND,
						MessageFormat.format("해당 username 이 {0}인 사용자가 존재하지 않습니다", username))),
			lastIdx
		);

		if (myFollowingIds.isEmpty()) {
			return Collections.emptyList();
		}

		Set<Long> authFollowings = followService.findAuthFollowings(authId, myFollowingIds);

		return memberRepository.findByIdInOrderById(myFollowingIds).stream()
			.map(member -> {
				boolean isMe = member.getId().equals(authId);

				if (authFollowings.contains(member.getId())) {
					return memberConverter.toFollowings(member, true, isMe);
				}

				return memberConverter.toFollowings(member, false, isMe);
			})
			.toList();
	}
}
