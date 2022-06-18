package com.kdt.instakyuram.post.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kdt.instakyuram.member.domain.Member;
import com.kdt.instakyuram.member.dto.MemberResponse;
import com.kdt.instakyuram.member.service.MemberGiver;
import com.kdt.instakyuram.post.domain.Post;
import com.kdt.instakyuram.post.domain.PostRepository;
import com.kdt.instakyuram.post.dto.PostConverter;
import com.kdt.instakyuram.post.dto.PostResponse;

// TODO : MemberGiver의 메서드가 필요합니다 !
@Transactional(readOnly = true)
@Service
public class PostService {

	private final PostRepository postRepository;
	private final PostConverter postConverter;
	private final MemberGiver memberGiver;

	public PostService(PostRepository postRepository, PostConverter postConverter, MemberGiver memberGiver) {
		this.postRepository = postRepository;
		this.postConverter = postConverter;
		this.memberGiver = memberGiver;
	}

	@Transactional
	public PostResponse.CreateResponse create(Long memberId, String content) {
		// 멤버 조회하기
		Member member = postConverter.toMember(
			memberGiver.findById(memberId)
		);

		//userService.findById() 유저 정보 조회 해오기
		Post savePost = postRepository.save(
			new Post(content, member)
		);

		return new PostResponse.CreateResponse(savePost.getId(), memberId, content);
	}

	public List<PostResponse.FindAllResponse> findAll(Long memberId) {
		List<MemberResponse> follows = memberGiver.findAllFollowing(memberId);
		List<Member> members = follows.stream()
			.map(follow -> postConverter.toMember(follow))
			.toList();

		return postRepository.findByMemberIn(members).stream()
			.map(postConverter::toResponse)
			.toList();
	}

}
