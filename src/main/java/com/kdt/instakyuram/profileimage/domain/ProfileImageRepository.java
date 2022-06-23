package com.kdt.instakyuram.profileimage.domain;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kdt.instakyuram.member.domain.Member;

public interface ProfileImageRepository extends JpaRepository<ProfileImage, Long> {
	Optional<ProfileImage> findByMember(Member member);
}
