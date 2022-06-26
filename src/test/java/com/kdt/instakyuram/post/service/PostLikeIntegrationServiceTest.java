package com.kdt.instakyuram.post.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.kdt.instakyuram.exception.BusinessException;
import com.kdt.instakyuram.member.domain.Member;
import com.kdt.instakyuram.post.domain.Post;
import com.kdt.instakyuram.post.domain.PostLike;
import com.kdt.instakyuram.post.domain.PostLikeRepository;
import com.kdt.instakyuram.post.dto.PostConverter;
import com.kdt.instakyuram.post.dto.PostLikeResponse;
import com.kdt.instakyuram.post.dto.PostResponse;

@SpringBootTest
class PostLikeIntegrationServiceTest {

	@Autowired
	private EntityManager entityManager;

	@Autowired
	private PostLikeRepository postLikeRepository;

	@Autowired
	private PostLikeService postLikeService;

	@Autowired
	private PostConverter postConverter;

	private List<Post> POSTS = new ArrayList<>();
	private List<Member> MEMBERS = new ArrayList<>();

	@BeforeEach
	void setUp() {
		entityManager.clear();
		postLikeRepository.deleteAll();
		MEMBERS = this.getDemoMembers();
		POSTS = this.getDemoPosts(MEMBERS);
	}

	@Test
	@Transactional
	@DisplayName("사용자가 좋아요를 한다.")
	void like() {
		//given
		Member member = MEMBERS.get(0);
		Post post = POSTS.get(0);

		//when
		PostLikeResponse savedLikeResponse = postLikeService.like(post.getId(), member.getId());

		//then
		assertThat(savedLikeResponse.postId()).isEqualTo(post.getId());
		assertThat(savedLikeResponse.likes()).isEqualTo(1);
		assertThat(savedLikeResponse.isLiked()).isTrue();
	}

	@Test
	@Transactional
	@DisplayName("이미 좋아요한 post에 다시 좋아요 요청을 하면 예외가 발생한다.")
	void like_fail() {
		//given
		Member member = MEMBERS.get(0);
		Post post = POSTS.get(0);
		PostLike like = PostLike.builder()
			.member(member)
			.post(post)
			.build();
		postLikeRepository.save(like);
		//when
		assertThatThrownBy(() -> postLikeService.like(post.getId(), member.getId()))
			.isInstanceOf(BusinessException.class);
	}

	@Test
	@Transactional
	@DisplayName("사용자가 좋아요를 취소한다.")
	void unlike() {
		//given
		Member member = MEMBERS.get(0);
		Post post = POSTS.get(0);
		PostLike like = PostLike.builder()
			.member(member)
			.post(post)
			.build();
		postLikeRepository.save(like);
		PostResponse postResponse = postConverter.toResponse(post);
		//when
		PostLikeResponse unlike = postLikeService.unlike(postResponse, member.getId());

		//then
		assertThat(unlike.isLiked()).isFalse();
		assertThat(unlike.postId()).isEqualTo(post.getId());
		assertThat(unlike.likes()).isZero();
	}

	@Test
	@Transactional
	@DisplayName("사용자가 좋아요하지 않은 post에 좋아요 취소 요청시 예외가 발생한다.")
	void unlike_fail() {
		//given
		Member member = MEMBERS.get(0);
		Post post = POSTS.get(0);
		PostResponse postResponse = postConverter.toResponse(post);

		//when, Then
		assertThatThrownBy(() -> postLikeService.unlike(postResponse, member.getId()))
			.isInstanceOf(BusinessException.class);
	}

	@Test
	@Transactional
	@DisplayName("해당 post의 좋아요를 모두 삭제한다.")
	void delete() {
		//given
		Member member1 = MEMBERS.get(0);
		Member member2 = MEMBERS.get(1);
		Post post = POSTS.get(0);
		PostLike like1 = PostLike.builder().post(post).member(member1).build();
		PostLike like2 = PostLike.builder().post(post).member(member2).build();
		postLikeRepository.save(like1);
		postLikeRepository.save(like2);

		//when
		postLikeService.delete(post.getId());

		//then
		List<PostLike> foundLikes = postLikeRepository.findByPostId(post.getId());
		assertThat(foundLikes).isEmpty();
	}

	@Test
	@Transactional
	@DisplayName("해당 post들의 모든 좋아요를 반환한다.")
	void findByPostIn() {
		//given
		Member member = MEMBERS.get(0);
		Post post1 = POSTS.get(0);
		Post post2 = POSTS.get(1);
		PostLike like1 = PostLike.builder().post(post1).member(member).build();
		PostLike like2 = PostLike.builder().post(post2).member(member).build();
		PostLikeResponse savedLike1 = postConverter.toPostLikeResponse(postLikeRepository.save(like1));
		PostLikeResponse savedLike2 = postConverter.toPostLikeResponse(postLikeRepository.save(like2));

		//when
		List<PostLikeResponse> foundLikes = postLikeService.findByPostIn(List.of(post1, post2));

		//then
		assertThat(foundLikes)
			.hasSize(2)
			.contains(savedLike1, savedLike2);
	}

	@Test
	@Transactional
	@DisplayName("해당 post의 총 좋아요 수를 반환한다.")
	void countByPostId() {
		//given
		Post post = POSTS.get(0);
		PostLike like1 = PostLike.builder().post(post).member(MEMBERS.get(0)).build();
		PostLike like2 = PostLike.builder().post(post).member(MEMBERS.get(1)).build();
		postLikeRepository.save(like1);
		postLikeRepository.save(like2);

		//when
		int totalLikes = postLikeService.countByPostId(post.getId());

		//then
		assertThat(totalLikes).isEqualTo(2);

	}

	@Transactional
	public List<Member> getDemoMembers() {
		Member member1 = Member.builder()
			.username("kevin123")
			.password("123456")
			.name("KEVIN")
			.phoneNumber("01012345678")
			.email("KEVIN@programmers.co.kr")
			.build();

		Member member2 = Member.builder()
			.username("tom123")
			.name("TOM")
			.password("123456")
			.phoneNumber("01012345678")
			.email("TOM@programmers.co.kr")
			.build();

		this.entityManager.persist(member1);
		this.entityManager.persist(member2);

		return List.of(member1, member2);
	}

	@Transactional
	public List<Post> getDemoPosts(List<Member> members) {
		Member member1 = members.get(0);
		Post post1 = Post.builder().content("게시글 1").member(member1).build();
		Post post2 = Post.builder().content("게시글 2").member(member1).build();
		Post post3 = Post.builder().content("게시글 3").member(member1).build();
		this.entityManager.persist(post1);
		this.entityManager.persist(post2);
		this.entityManager.persist(post3);

		return List.of(post1, post2, post3);
	}
}