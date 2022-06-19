package com.kdt.instakyuram.follow;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.kdt.instakyuram.follow.domain.Follow;
import com.kdt.instakyuram.follow.domain.FollowRepository;
import com.kdt.instakyuram.follow.service.FollowService;
import com.kdt.instakyuram.member.domain.Member;
import com.kdt.instakyuram.member.domain.MemberRepository;

@SpringBootTest
public class IntegrationTest {

	@Autowired
	private FollowService followService;

	@Autowired
	private FollowRepository followRepository;

	@Autowired
	private MemberRepository memberRepository;

	@Test
	@DisplayName("팔로잉 목록 조회 테스트")
	void testFollowing() {
		//given
		List<Member> members = this.getDemoMembers();

		Member member = members.get(0);
		Member targetA = members.get(1);
		Member targetB = members.get(2);

		List<Follow> followings = followRepository.saveAll(List.of(
			Follow.builder()
				.memberId(member.getId())
				.targetId(targetA.getId())
				.build(),

			Follow.builder()
				.memberId(member.getId())
				.targetId(targetB.getId())
				.build()
		));

		//when
		List<Long> followingIds = followService.findByFollowingIds(member.getId());

		//then
		assertThat(followingIds.size()).isEqualTo(followings.size());
		assertThat(followingIds).contains(targetA.getId(), targetB.getId());
	}

	private List<Member> getDemoMembers() {

		List<Member> follwings = new ArrayList<>();

		String name = "programmers";
		String password = "password";
		String phoneNumber = "01012345678";
		String emailPostfix = "@programmers.co.kr";

		IntStream.range(1, 5).forEach(
			number -> {
				Member persistedMember = memberRepository.save(
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

}
