package com.kdt.instakyuram.member.service;

import java.util.UUID;

import org.apache.commons.io.FilenameUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.kdt.instakyuram.follow.service.FollowService;
import com.kdt.instakyuram.member.dto.MemberResponse;
import com.kdt.instakyuram.post.service.PostService;

@Service
@Transactional(readOnly = true)
public class ProfileService {

	private final MemberService memberService;
	private final PostService postService;
	private final FollowService followService;
	private final ProfileImageService profileImageService;

	public ProfileService(MemberService memberService, PostService postService, FollowService followService,
		ProfileImageService profileImageService) {
		this.memberService = memberService;
		this.postService = postService;
		this.followService = followService;
		this.profileImageService = profileImageService;
	}

	public MemberResponse.ProfileInfoResponse findProfileInfoByUsername(String username) {
		MemberResponse foundMember = memberService.findByUsername(username);
		return new MemberResponse.ProfileInfoResponse(
			foundMember.id(),
			foundMember.username(),
			foundMember.name(),
			foundMember.introduction(),
			(long)this.postService.findAllByMemberId(foundMember.id()).size(),
			this.followService.countMyFollower(foundMember.id()),
			this.followService.countMyFollowing(foundMember.id()),
			foundMember.profileImageName()
		);
	}

	@Transactional
	public String updateProfileImage(Long memberId, MultipartFile profileImage) {
		MemberResponse member = memberService.findById(memberId);
		String profileImageName = member.profileImageName();
		if (profileImageName == null) {
			profileImageName = UUID.randomUUID() + FilenameUtils.getExtension(profileImage.getOriginalFilename());
		}

		this.profileImageService.update(profileImage, profileImageName);
		return this.memberService.updateProfileImage(memberId, profileImageName);
	}

	public FileSystemResource findProfileImage(Long memberId) {
		MemberResponse member = this.memberService.findById(memberId);
		System.out.println(member.profileImageName());
		return this.profileImageService.findProfileImage(member.profileImageName());
	}
}

