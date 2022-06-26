package com.kdt.instakyuram.post.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.kdt.instakyuram.comment.dto.CommentResponse;
import com.kdt.instakyuram.comment.service.CommentGiver;
import com.kdt.instakyuram.member.domain.Member;
import com.kdt.instakyuram.member.dto.MemberResponse;
import com.kdt.instakyuram.member.service.MemberGiver;
import com.kdt.instakyuram.post.domain.Post;
import com.kdt.instakyuram.post.domain.PostImage;
import com.kdt.instakyuram.post.domain.PostRepository;
import com.kdt.instakyuram.post.dto.PostConverter;
import com.kdt.instakyuram.post.dto.PostImageResponse;
import com.kdt.instakyuram.post.dto.PostLikeResponse;
import com.kdt.instakyuram.post.dto.PostResponse;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

	@InjectMocks
	private PostService postService;
	@Mock
	private PostRepository postRepository;
	@Mock
	private PostConverter postConverter;
	@Mock
	private MemberGiver memberGiver;
	@Mock
	private PostImageService postImageService;
	@Mock
	private CommentGiver commentGiver;
	@Mock
	private PostLikeService postLikeService;

	private final List<MemberResponse> MEMBER_RESPONSES = this.getDemoMemberResponses();
	private final List<Member> MEMBERS = this.getDemoMembers();
	private final List<Post> POSTS = this.getDemoPosts();
	private final List<PostResponse> POST_RESPONSES = this.getDemoPostResponses();
	private final List<PostResponse.FindAllResponse> POST_FIND_ALL_RESPONSES = this.getDemoPostFindAllResponses();
	private final List<CommentResponse> COMMENT_RESPONSES = this.getDemoCommentResponses();
	private final List<PostLikeResponse> POST_LIKE_RESPONSES = this.getDemoPostLikeResponses();
	private final List<PostImageResponse> POST_IMAGE_RESPONSES = this.getDemoPostImageResponses();

	MockMultipartFile MOCK_IMAGE1 = new MockMultipartFile("image1", "sample.jpg", ".jpg",
		"demo image1".getBytes());
	MockMultipartFile MOCK_IMAGE2 = new MockMultipartFile("image2", "sample.jpg", ".jpg",
		"demo image2".getBytes());
	private final Member MEMBER = MEMBERS.get(0);
	private final MemberResponse MEMBER_RESPONSE = MEMBER_RESPONSES.get(0);

	@Test
	@DisplayName("내가 팔로우 한 모든 사람들의 게시글과 내 게시글을 조회한다.")
	void findAllRelated() {
		Post post1 = POSTS.get(0);
		Post post2 = POSTS.get(1);
		Post post3 = POSTS.get(2);

		Map<Long, List<CommentResponse>> commentByPostId = COMMENT_RESPONSES.stream()
			.collect(Collectors.groupingBy(CommentResponse::postId));
		Map<Long, List<PostImageResponse>> imageByPostId = POST_IMAGE_RESPONSES.stream()
			.collect(Collectors.groupingBy(PostImageResponse::postId));

		given(memberGiver.findAllFollowingIncludeMe(MEMBER.getId())).willReturn(MEMBER_RESPONSES);
		given(postConverter.toMember(MEMBER_RESPONSES.get(0))).willReturn(MEMBERS.get(0));
		given(postConverter.toMember(MEMBER_RESPONSES.get(1))).willReturn(MEMBERS.get(1));
		given(postConverter.toMember(MEMBER_RESPONSES.get(2))).willReturn(MEMBERS.get(2));
		given(postRepository.findByMemberIn(MEMBERS)).willReturn(POSTS);
		given(postImageService.findByPostIn(POSTS)).willReturn(POST_IMAGE_RESPONSES);
		given(commentGiver.findByPostIn(POSTS)).willReturn(COMMENT_RESPONSES);
		given(postLikeService.findByPostIn(POSTS)).willReturn(POST_LIKE_RESPONSES);
		given(postConverter.toMemberResponse(post1.getMember())).willReturn(MEMBER_RESPONSES.get(0));
		given(postConverter.toMemberResponse(post2.getMember())).willReturn(MEMBER_RESPONSES.get(1));
		given(postConverter.toMemberResponse(post3.getMember())).willReturn(MEMBER_RESPONSES.get(2));
		given(postConverter.toDetailResponse(
			MEMBER_RESPONSES.get(0),
			post1,
			imageByPostId.get(post1.getId()),
			commentByPostId.get(post1.getId()),
			0
		)).willReturn(POST_FIND_ALL_RESPONSES.get(0));
		given(postConverter.toDetailResponse(
			MEMBER_RESPONSES.get(1),
			post2,
			imageByPostId.get(post2.getId()),
			commentByPostId.get(post2.getId()),
			2
		)).willReturn(POST_FIND_ALL_RESPONSES.get(1));
		given(postConverter.toDetailResponse(
			MEMBER_RESPONSES.get(2),
			post3,
			imageByPostId.get(post3.getId()),
			commentByPostId.get(post3.getId()),
			2
		)).willReturn(POST_FIND_ALL_RESPONSES.get(2));

		//when
		List<PostResponse.FindAllResponse> foundPosts = postService.findAllRelated(MEMBER.getId());

		//then
		assertThat(foundPosts).hasSameElementsAs(POST_FIND_ALL_RESPONSES);
	}

	@Test
	@DisplayName("Post를 작성할 수 있다.")
	void create() {
		Post post = POSTS.get(0);
		List<MultipartFile> images = List.of(MOCK_IMAGE1);
		PostImage image1 = PostImage.builder()
			.post(post)
			.originalFileName(MOCK_IMAGE1.getOriginalFilename())
			.serverFileName(UUID.randomUUID() + "." + FilenameUtils.getExtension(MOCK_IMAGE1.getOriginalFilename()))
			.path(System.getProperty("user.dir") + "/picture/")
			.size(MOCK_IMAGE1.getSize())
			.build();
		PostImage image2 = PostImage.builder()
			.post(post)
			.originalFileName(MOCK_IMAGE2.getOriginalFilename())
			.serverFileName(UUID.randomUUID() + "." + FilenameUtils.getExtension(MOCK_IMAGE2.getOriginalFilename()))
			.path(System.getProperty("user.dir") + "/picture/")
			.size(MOCK_IMAGE2.getSize())
			.build();
		Map<PostImage, MultipartFile> postImagesMap = Map.of(image1, MOCK_IMAGE1, image2, MOCK_IMAGE2);
		PostResponse.CreateResponse createResponse = new PostResponse.CreateResponse(post.getId(), MEMBER.getId(),
			post.getContent());

		given(memberGiver.findById(MEMBER.getId())).willReturn(MEMBER_RESPONSE);
		given(postConverter.toMember(MEMBER_RESPONSE)).willReturn(MEMBER);
		given(postRepository.save(any())).willReturn(post);
		willDoNothing().given(postImageService).save(post.getId(), images);

		//when
		PostResponse.CreateResponse response = postService.create(MEMBER.getId(), post.getContent(), images);

		//then
		assertThat(response).isEqualTo(createResponse);
	}

	@Test
	@DisplayName("Post를 수정할 수 있다.")
	void update() {
		Post post = POSTS.get(0);
		String content = "게시글 수정하는 테스트입니다.";

		PostResponse.UpdateResponse response =
			new PostResponse.UpdateResponse(post.getId(), post.getContent());

		// GIVEN
		given(postRepository.findByIdAndMemberId(post.getId(), post.getMember().getId()))
			.willReturn(Optional.of(post));
		given(postConverter.toUpdateResponse(post)).willReturn(response);

		// WHEN
		PostResponse.UpdateResponse updatedResponse = postService.update(post.getId(),
			post.getMember().getId(), content);

		// THEN
		assertThat(updatedResponse).isEqualTo(response);
		verify(postRepository, times(1))
			.findByIdAndMemberId(post.getId(), post.getMember().getId());
		verify(postConverter, times(1)).toUpdateResponse(post);
	}

	private List<Member> getDemoMembers() {
		List<Member> members = new ArrayList<>();
		members.add(
			Member.builder()
				.id(1L)
				.username("KEVIN")
				.name("KEVIN")
				.phoneNumber("01012345678")
				.email("KEVIN@programmers.co.kr")
				.build());

		members.add(
			Member.builder()
				.id(2L)
				.username("TOM")
				.name("TOM")
				.phoneNumber("01012345678")
				.email("TOM@programmers.co.kr")
				.build()
		);

		members.add(
			Member.builder()
				.id(3L)
				.username("MARIA")
				.name("MARIA")
				.phoneNumber("01012345678")
				.email("MARIA@programmers.co.kr")
				.build()
		);

		return members;
	}

	private List<MemberResponse> getDemoMemberResponses() {
		return this.getDemoMembers().stream()
			.map(member ->
				MemberResponse.builder()
					.id(member.getId())
					.username(member.getUsername())
					.name(member.getName())
					.phoneNumber(member.getPhoneNumber())
					.email(member.getEmail())
					.build())
			.toList();
	}

	private List<Post> getDemoPosts() {
		List<Post> posts = new ArrayList<>();
		List<Member> members = MEMBERS;
		posts.add(Post.builder().id(10L).content("게시글 1").member(members.get(0)).build());
		posts.add(Post.builder().id(11L).content("게시글 2").member(members.get(1)).build());
		posts.add(Post.builder().id(12L).content("게시글 3").member(members.get(2)).build());

		return posts;
	}

	private List<PostResponse> getDemoPostResponses() {
		return this.getDemoPosts().stream()
			.map(post -> {
				Member member = post.getMember();
				MemberResponse memberResponse = MemberResponse.builder()
					.id(member.getId())
					.username(member.getUsername())
					.name(member.getName())
					.phoneNumber(member.getPhoneNumber())
					.email(member.getEmail())
					.build();

				return PostResponse.builder()
					.id(post.getId())
					.content(post.getContent())
					.memberResponse(memberResponse)
					.build();
			}).toList();
	}

	private List<PostResponse.FindAllResponse> getDemoPostFindAllResponses() {
		List<PostResponse.FindAllResponse> postFindAllResponses = new ArrayList<>();
		Map<Long, List<CommentResponse>> commentByPostId = this.getDemoCommentResponses().stream()
			.collect(Collectors.groupingBy(CommentResponse::postId));
		Map<Long, List<PostImageResponse>> imageByPostId = this.getDemoPostImageResponses().stream()
			.collect(Collectors.groupingBy(PostImageResponse::postId));
		Post post1 = POSTS.get(0);
		Post post2 = POSTS.get(1);
		Post post3 = POSTS.get(2);

		postFindAllResponses.add(
			PostResponse.FindAllResponse.builder()
				.id(post1.getId())
				.content(post1.getContent())
				.member(MEMBER_RESPONSES.get(0))
				.postImageResponse(imageByPostId.get(post1.getId()))
				.commentResponse(commentByPostId.get(post1.getId()))
				.totalPostLike(0)
				.build());

		postFindAllResponses.add(
			PostResponse.FindAllResponse.builder()
				.id(post2.getId())
				.content(post2.getContent())
				.member(MEMBER_RESPONSES.get(1))
				.postImageResponse(imageByPostId.get(post2.getId()))
				.commentResponse(commentByPostId.get(post2.getId()))
				.totalPostLike(2)
				.build());

		postFindAllResponses.add(
			PostResponse.FindAllResponse.builder()
				.id(post3.getId())
				.content(post3.getContent())
				.member(MEMBER_RESPONSES.get(2))
				.postImageResponse(imageByPostId.get(post3.getId()))
				.commentResponse(commentByPostId.get(post3.getId()))
				.totalPostLike(2)
				.build());

		return postFindAllResponses;
	}

	private List<CommentResponse> getDemoCommentResponses() {
		List<CommentResponse> commentResponses = new ArrayList<>();
		commentResponses.add(new CommentResponse(1L, 11L, "댓글 1", MEMBER_RESPONSE));
		commentResponses.add(new CommentResponse(2L, 12L, "댓글 2", MEMBER_RESPONSE));
		commentResponses.add(new CommentResponse(3L, 11L, "댓글 3", MEMBER_RESPONSE));
		commentResponses.add(new CommentResponse(4L, 12L, "댓글 4", MEMBER_RESPONSE));

		return commentResponses;
	}

	private List<PostLikeResponse> getDemoPostLikeResponses() {
		List<PostLikeResponse> postLikeResponses = new ArrayList<>();
		postLikeResponses.add(PostLikeResponse.builder().postId(11L).build());
		postLikeResponses.add(PostLikeResponse.builder().postId(12L).build());
		postLikeResponses.add(PostLikeResponse.builder().postId(11L).build());
		postLikeResponses.add(PostLikeResponse.builder().postId(12L).build());

		return postLikeResponses;
	}

	private List<PostImageResponse> getDemoPostImageResponses() {
		PostImageResponse image1 = PostImageResponse.builder().id(1L).postId(11L).build();
		PostImageResponse image2 = PostImageResponse.builder().id(2L).postId(12L).build();

		return List.of(image1, image2);
	}
}