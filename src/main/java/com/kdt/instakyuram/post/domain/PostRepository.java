package com.kdt.instakyuram.post.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kdt.instakyuram.member.domain.Member;

public interface PostRepository extends JpaRepository<Post, Long> {

	List<Post> findByMemberIn(List<Member> members);

}
