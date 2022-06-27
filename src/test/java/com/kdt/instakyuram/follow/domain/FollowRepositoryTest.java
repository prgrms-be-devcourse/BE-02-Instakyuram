package com.kdt.instakyuram.follow.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import org.assertj.core.api.Assertions;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.kdt.instakyuram.member.domain.Member;

@DataJpaTest
class FollowRepositoryTest {

	@Autowired
	private TestEntityManager entityManager;

	@Autowired
	private FollowRepository followRepository;

	@BeforeEach
	private void eachInit() {
		followRepository.deleteAll();
	}

	@Test
	@DisplayName("팔로잉 목록 조회 테스트")
	void testLookUpFollowings() {
		//given
		List<Member> members = getMembers();

		Member member = members.get(0);
		Member targetA = members.get(1);
		Member targetB = members.get(2);

		followRepository.save(Follow.builder()
			.memberId(member.getId())
			.targetId(targetA.getId())
			.build());

		followRepository.save(Follow.builder()
			.memberId(member.getId())
			.targetId(targetB.getId())
			.build());

		//when
		List<Long> followingIds = followRepository.findByMemberId(member.getId()).stream()
			.map(Follow::getTargetId)
			.toList();

		//then
		assertThat(followingIds.size()).isEqualTo(2);
		assertThat(followingIds).contains(targetA.getId(), targetB.getId());
	}

	@Test
	@DisplayName("나를 따르는 사람의 수 구하기 =: follower 수")
	void testCountMyFollower() {
		//given
		List<Member> members = getMembers();

		Member my = members.get(0);
		Member fromA = members.get(1);
		Member fromB = members.get(2);
		Member fromC = members.get(3);

		followRepository.save(Follow.builder()
			.memberId(fromA.getId())
			.targetId(my.getId())
			.build());

		followRepository.save(Follow.builder()
			.memberId(fromB.getId())
			.targetId(my.getId())
			.build());

		followRepository.save(Follow.builder()
			.memberId(fromC.getId())
			.targetId(my.getId())
			.build());

		//when
		long followerCount = followRepository.countByTargetId(my.getId());

		//then
		Assertions.assertThat(followerCount).isEqualTo(3);
	}

	@Test
	@DisplayName("내가 따르는 사람의 수 구하기 =: following 수")
	void testCountMyFollowing() {
		//given
		List<Member> members = getMembers();

		Member my = members.get(0);
		Member targetA = members.get(1);
		Member targetB = members.get(2);

		followRepository.save(Follow.builder()
			.memberId(my.getId())
			.targetId(targetA.getId())
			.build());

		followRepository.save(Follow.builder()
			.memberId(my.getId())
			.targetId(targetB.getId())
			.build());

		//when
		long followerCount = followRepository.countByMemberId(my.getId());
		//then
		Assertions.assertThat(followerCount).isEqualTo(2);
	}

	private List<Member> getMembers() {

		List<Member> follwings = new ArrayList<>();

		String name = "programmers";
		String password = "password";
		String phoneNumber = "01012345678";
		String emailPostfix = "@programmers.co.kr";

		IntStream.range(1, 5).forEach(
			number -> {
				Member persistedMember = entityManager.persist(
					Member.builder()
						.email((name + number) + emailPostfix)
						.password(password)
						.username(name + number)
						.phoneNumber(phoneNumber)
						.name(name)
						.build()
				);

				follwings.add(persistedMember);
			}
		);

		return follwings;
	}

	@Test
	@DisplayName("40명이 팔로워 하는 상황에서 회원 번호 순 30번째 이후에 팔로워 대상을 조회한다. [30개씩 가져옴] - 무한 스크롤 쿼리")
	void testFindTop30ByTargetIdAndMemberIdGreaterThanOrderByMemberId() {
		int gettingSize = 30;
		List<Member> members = setUp41Member();
		Member targetMember = members.remove(0);
		long alreadyLookUpId = members.get(gettingSize - 1).getId();

		members.forEach(member ->
			followRepository.save(Follow.builder()
				.memberId(member.getId())
				.targetId(targetMember.getId())
				.build()));

		List<Long> expectedMemberIds = LongStream.rangeClosed(alreadyLookUpId + 1, alreadyLookUpId + gettingSize)
			.boxed()
			.toList();

		//when
		List<Follow> followers = followRepository.findTop30ByTargetIdAndMemberIdGreaterThanOrderByMemberId(
			targetMember.getId(),
			alreadyLookUpId);
		List<Long> followIds = followers.stream().map(Follow::getId).toList();

		//then
		assertThat(followers.size()).isEqualTo(members.size() - gettingSize);
		MatcherAssert.assertThat(followIds, Matchers.samePropertyValuesAs(expectedMemberIds));
	}

	@Test
	@DisplayName("40명이 팔로워 하는 상황에서 회원 번호 순 30번째 이후에 팔로워 대상을 조회한다. [30개씩 가져옴] - 무한 스크롤 쿼리")
	void TestFindTop30ByMemberIdAndTargetIdGreaterThanOrderByTargetId() {
		//given
		int gettingSize = 30;
		List<Member> members = setUp41Member();
		Member member = members.remove(0);
		long alreadyLookUpId = members.get(gettingSize-1).getId();

		members.forEach(targetMember -> {
			followRepository.save(Follow.builder()
				.memberId(member.getId())
				.targetId(targetMember.getId())
				.build());
		});

		List<Long> expectedFollowingIds = LongStream.rangeClosed(alreadyLookUpId + 1, alreadyLookUpId + gettingSize)
			.boxed()
			.toList();

		//when
		List<Follow> followings = followRepository.findTop30ByMemberIdAndTargetIdGreaterThanOrderByTargetId(
			member.getId(), alreadyLookUpId);

		List<Long> followingIds = followings.stream().map(Follow::getTargetId).toList();

		//then
		Assertions.assertThat(followings.size()).isEqualTo(members.size() - gettingSize);
		MatcherAssert.assertThat(followingIds, Matchers.samePropertyValuesAs(expectedFollowingIds));

	}

	private List<Member> setUp41Member() {
		List<Member> members = new ArrayList<>();

		String name = "programmers";
		String password = "password";
		String phoneNumber = "01012345678";
		String emailPostfix = "@programmers.co.kr";

		IntStream.rangeClosed(1, 41).forEach(
			number -> {
				Member persistedMember = entityManager.persist(
					Member.builder()
						.email((name + number) + emailPostfix)
						.password(password)
						.username(name + number)
						.phoneNumber(phoneNumber)
						.name(name)
						.build()
				);

				members.add(persistedMember);
			}
		);

		return members;
	}

}