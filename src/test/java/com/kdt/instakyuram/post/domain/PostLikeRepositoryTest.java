package com.kdt.instakyuram.post.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.kdt.instakyuram.member.domain.Member;

@DataJpaTest
class PostLikeRepositoryTest {

	@Autowired
	private TestEntityManager testEntityManager;

	@Autowired
	private PostLikeRepository postLikeRepository;

	private List<Member> MEMBERS = new ArrayList<>();
	private List<Post> POSTS = new ArrayList<>();

	@BeforeEach
	void setUp() {
		this.testEntityManager.clear();
		postLikeRepository.deleteAll();
		MEMBERS = this.getDemoMembers();
		POSTS = this.getDemoPosts(MEMBERS);
	}

	@Test
	@DisplayName("해당 post Id를 가진 좋아요를 반환한다.")
	void findByPostId() {
		//given
		Post post = POSTS.get(0);
		Member member = MEMBERS.get(1);
		PostLike like1 = postLikeRepository.save(PostLike.builder()
			.post(post)
			.member(member)
			.build());
		PostLike like2 = postLikeRepository.save(PostLike.builder()
			.post(post)
			.member(member)
			.build());
		List<PostLike> likes = List.of(like1, like2);

		//when
		List<PostLike> foundLikes = postLikeRepository.findByPostId(post.getId());

		//then
		assertThat(foundLikes).hasSameElementsAs(likes);
	}

	@Test
	@DisplayName("해당 post의 좋아요 수를 반환한다.")
	void countByPostId() {
		//given
		Post post = POSTS.get(0);
		Member member = MEMBERS.get(0);
		PostLike like1 = PostLike.builder()
			.post(post)
			.member(member)
			.build();
		PostLike like2 = PostLike.builder()
			.post(post)
			.member(member)
			.build();
		postLikeRepository.save(like1);
		postLikeRepository.save(like2);
		int likeCount = 2;

		//when
		int count = postLikeRepository.countByPostId(post.getId());

		//then
		assertThat(count).isEqualTo(likeCount);
	}

	@Test
	@DisplayName("해당 유저가 해당 포스트에 남긴 좋아요를 반환한다.")
	void findByPostIdAndMemberId() {
		//given
		Member member = MEMBERS.get(1);
		Post post = POSTS.get(0);
		PostLike like = postLikeRepository.save(PostLike.builder()
			.post(post)
			.member(member)
			.build());

		//when
		Optional<PostLike> foundLike = postLikeRepository.findByPostIdAndMemberId(post.getId(), member.getId());

		//then
		assertThat(foundLike)
			.isPresent()
			.contains(like);

	}

	@Test
	@DisplayName("해당 postId와 MemberId를 가진 Like가 존재하는지 boolean 반환")
	void existsPostLikeByPostIdAndMemberId() {
		//given
		Member member = MEMBERS.get(1);
		Post post = POSTS.get(0);
		PostLike like = PostLike.builder()
			.post(post)
			.member(member)
			.build();
		postLikeRepository.save(like);

		//when
		boolean isExist = postLikeRepository.existsPostLikeByPostIdAndMemberId(post.getId(), member.getId());

		//then
		assertThat(isExist).isTrue();

	}

	@Test
	@DisplayName("해당 포스트의 모든 좋아요를 반환한다.")
	void findByPostIn() {
		//given
		PostLike like1 = postLikeRepository.save(PostLike.builder().post(POSTS.get(0)).member(MEMBERS.get(1)).build());
		PostLike like2 = postLikeRepository.save(PostLike.builder().post(POSTS.get(1)).member(MEMBERS.get(1)).build());
		PostLike like3 = postLikeRepository.save(PostLike.builder().post(POSTS.get(2)).member(MEMBERS.get(1)).build());
		List<PostLike> likes = List.of(like1, like2, like3);

		//when
		List<PostLike> foundLikes = postLikeRepository.findByPostIn(POSTS);

		//then
		assertThat(likes).hasSameElementsAs(foundLikes);

	}

	private List<Member> getDemoMembers() {
		Member member1 = this.testEntityManager.persist(
			Member.builder()
				.username("kevin123")
				.password("123456")
				.name("KEVIN")
				.phoneNumber("01012345678")
				.email("KEVIN@programmers.co.kr")
				.build());

		Member member2 = this.testEntityManager.persist(Member.builder()
			.username("tom123")
			.name("TOM")
			.password("123456")
			.phoneNumber("01012345678")
			.email("TOM@programmers.co.kr")
			.build());

		return List.of(member1, member2);
	}

	private List<Post> getDemoPosts(List<Member> members) {
		Member member1 = members.get(0);

		Post post1 = this.testEntityManager.persist(Post.builder().content("게시글 1").member(member1).build());
		Post post2 = this.testEntityManager.persist(Post.builder().content("게시글 2").member(member1).build());
		Post post3 = this.testEntityManager.persist(Post.builder().content("게시글 3").member(member1).build());

		return List.of(post1, post2, post3);
	}
}