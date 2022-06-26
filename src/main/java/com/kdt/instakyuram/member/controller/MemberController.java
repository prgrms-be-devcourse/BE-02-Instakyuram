package com.kdt.instakyuram.member.controller;

import javax.validation.Valid;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.kdt.instakyuram.common.PageDto;
import com.kdt.instakyuram.exception.NotAuthenticationException;
import com.kdt.instakyuram.member.dto.MemberResponse;
import com.kdt.instakyuram.member.service.MemberService;
import com.kdt.instakyuram.member.service.ProfileService;
import com.kdt.instakyuram.post.service.PostGiver;
import com.kdt.instakyuram.security.jwt.JwtAuthentication;

@RequestMapping("/members")
@Controller
public class MemberController {
	private final MemberService memberService;
	private final PostGiver postGiver;
	private final ProfileService profileService;

	public MemberController(MemberService memberService, PostGiver postGiver, ProfileService profileService) {
		this.memberService = memberService;
		this.postGiver = postGiver;
		this.profileService = profileService;
	}

	//todo: @AuthenticationPrincipal JwtAuthentication member로 요청 id 뽑아내기 -> 테스트 코드 변경
	@GetMapping("/all")
	public RedirectView firstRequestMembers() {
		return new RedirectView("/members?page=1&size=10");
	}

	//todo: @AuthenticationPrincipal JwtAuthentication member로 요청 id 뽑아내기 -> 테스트 코드 변경
	@GetMapping
	public ModelAndView getMembers(@ModelAttribute @Valid PageDto.Request pagingDto) {
		Pageable requestPage = pagingDto.getPageable(Sort.by("id").descending());

		return new ModelAndView("member/member-list")
			.addObject(memberService.findAll(requestPage));
	}

	@GetMapping("/{username}")
	public String personalPage(@PathVariable String username, Model model,
		@AuthenticationPrincipal JwtAuthentication auth) {
		MemberResponse foundMember = memberService.findByUsername(username);

		if (auth != null) {
			model.addAttribute("auth", auth.id().equals(foundMember.id()));
		}
		model.addAttribute("thumbnails", postGiver.findPostThumbnailsByMemberId(foundMember.id()));
		model.addAttribute("profileInfo", profileService.findProfileInfoByUsername(username));

		return "personal-page";
	}

	/**
	 * todo: username이 auth가 될수도 있고 아닐 수도 있는 상황에서
	 *  username 팔로워(나를 따르는 사람들)들을 조회하고 나서
	 *  내가 이미 팔로잉한 대상인지 여부에 따라 팔로우 언팔로우 버튼이 노출되어야 한다.
	 * note: 나의 프로필에서 팔로워를 조회하는데 내가 팔로잉을 안할 수도 있자나?
	 * 	 * 	그러면 팔로우 버튼이 노출되야 함.
	 * @param username
	 * @param auth
	 * @return
	 */
	@GetMapping("/{username}/followers")
	public ModelAndView renderLookUpFollowers(@PathVariable String username,
		@AuthenticationPrincipal JwtAuthentication auth) {
		if (auth == null) {
			throw new NotAuthenticationException("로그인이 필요합니다.");
		}

		return new ModelAndView("modal/follower-list").addObject(
			"followers", memberService.getFollowers(auth.id(), username, 0L)
		);
	}

	@GetMapping("/{username}/followings")
	public ModelAndView renderLookUpFollowings(@PathVariable String username,
		@AuthenticationPrincipal JwtAuthentication auth) {
		if (auth == null) {
			throw new NotAuthenticationException("로그인이 필요합니다.");
		}

		return new ModelAndView("modal/following-list").addObject(
			"followings", memberService.getFollowings(auth.id(), username, 0L)
		);
	}

	@GetMapping("/signin")
	public String singinPage() {
		return "signin";
	}
}
