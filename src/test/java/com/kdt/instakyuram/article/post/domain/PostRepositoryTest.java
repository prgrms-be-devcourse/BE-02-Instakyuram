package com.kdt.instakyuram.article.post.domain;

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

import com.kdt.instakyuram.user.member.domain.Member;

@DataJpaTest
class PostRepositoryTest {

	@Autowired
	private TestEntityManager testEntityManager;

	@Autowired
	private PostRepository postRepository;

	@BeforeEach
	void setUp() {
		testEntityManager.clear();
		postRepository.deleteAll();
	}

	@Test
	@DisplayName("팔로잉한 멤버의 게시글 조회한다.")
	void findByMemberIn() {
		//given
		List<Member> followings = this.getDemoMembers();
		List<Post> posts = this.getDemoPosts(followings).stream()
			.map(post -> postRepository.save(post))
			.toList();

		//when
		List<Post> foundPosts = postRepository.findAllByMemberIn(followings);
		//then
		assertThat(foundPosts).hasSameElementsAs(posts);
	}

	@Test
	@DisplayName("해당 postId와 해당 memberId를 가진 post를 조회한다.")
	void findByIdAndMemberId() {
		//given
		List<Member> members = this.getDemoMembers();
		Member member = members.get(0);

		List<Post> posts = this.getDemoPosts(members).stream()
			.map(post -> postRepository.save(post))
			.filter(post -> post.getMember().getUsername().equals(member.getUsername()))
			.toList();
		Post findPost = posts.get(0);

		//when
		Optional<Post> foundPost = postRepository.findByIdAndMemberId(findPost.getId(), findPost.getMember().getId());

		//then
		assertThat(foundPost).isPresent();
		assertThat(foundPost.get()).usingRecursiveComparison().isEqualTo(findPost);

	}

	private List<Member> getDemoMembers() {
		List<Member> members = new ArrayList<>();

		members.add(
			Member.builder()
				.username("kevin123")
				.password("123456")
				.name("KEVIN")
				.phoneNumber("01012345678")
				.email("KEVIN@programmers.co.kr")
				.build());

		members.add(
			Member.builder()
				.username("tom123")
				.name("TOM")
				.password("123456")
				.phoneNumber("01012345678")
				.email("TOM@programmers.co.kr")
				.build()
		);

		return members;
	}

	private List<Post> getDemoPosts(List<Member> members) {
		List<Post> posts = new ArrayList<>();
		testEntityManager.persist(members.get(0));
		testEntityManager.persist(members.get(1));

		posts.add(Post.builder().content("게시글 1").member(members.get(0)).build());
		posts.add(Post.builder().content("게시글 2").member(members.get(0)).build());
		posts.add(Post.builder().content("게시글 3").member(members.get(0)).build());
		posts.add(Post.builder().content("게시글 4").member(members.get(1)).build());
		posts.add(Post.builder().content("게시글 5").member(members.get(1)).build());
		posts.add(Post.builder().content("게시글 6").member(members.get(1)).build());

		return posts;
	}
}