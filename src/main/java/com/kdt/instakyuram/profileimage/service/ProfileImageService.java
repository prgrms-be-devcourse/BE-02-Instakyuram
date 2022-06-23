package com.kdt.instakyuram.profileimage.service;

import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kdt.instakyuram.member.domain.Member;
import com.kdt.instakyuram.member.dto.MemberResponse;
import com.kdt.instakyuram.member.service.MemberService;
import com.kdt.instakyuram.profileimage.domain.ProfileImageRepository;
import com.kdt.instakyuram.util.ImageManager;

@Transactional(readOnly = true)
@Service
public class ProfileImageService {
	private final ProfileImageRepository profileImageRepository;
	private final MemberService memberService;

	public ProfileImageService(ProfileImageRepository profileImageRepository, MemberService memberService) {
		this.profileImageRepository = profileImageRepository;
		this.memberService = memberService;
	}

	public FileSystemResource findProfileImage(Long id, String serverFileName) {
		MemberResponse memberResponse = memberService.findById(id);

		return profileImageRepository.findByMember(
				Member.builder()
					.id(memberResponse.id())
					.build()
			).filter(memberImage -> memberImage.getServerFileName().equals(serverFileName))
			.map(profileImage -> ImageManager.getFileResource(profileImage.getPath(), profileImage.getServerFileName()))
			.orElseGet(ImageManager::getBasicImage);
	}
}
