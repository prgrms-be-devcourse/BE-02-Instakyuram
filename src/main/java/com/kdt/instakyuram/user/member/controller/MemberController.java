package com.kdt.instakyuram.user.member.controller;

import javax.validation.Valid;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.kdt.instakyuram.article.post.service.PostGiver;
import com.kdt.instakyuram.common.PageDto;
import com.kdt.instakyuram.exception.NotAuthenticationException;
import com.kdt.instakyuram.security.jwt.JwtAuthentication;
import com.kdt.instakyuram.user.member.dto.MemberOrderDto;
import com.kdt.instakyuram.user.member.dto.MemberResponse;
import com.kdt.instakyuram.user.member.service.MemberService;
import com.kdt.instakyuram.user.profile.service.ProfileService;

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

	@GetMapping("/all")
	public RedirectView firstRequestMembers() {
		return new RedirectView("/members?page=1&size=10");
	}

	@GetMapping
	public ModelAndView getMembers(@Valid PageDto.Request pagingDto, @Valid MemberOrderDto searchDto,
		@AuthenticationPrincipal JwtAuthentication auth) {
		if (auth == null) {
			throw new NotAuthenticationException("로그인을 하셔야 합니다..");
		}

		return new ModelAndView("member/member-list")
			.addObject("dto", memberService.findAll(auth.id(), pagingDto.getPageable(searchDto.getSortingCriteria())));
	}

	@GetMapping("/{username}")
	public String personalPage(@PathVariable String username, Model model,
		@AuthenticationPrincipal JwtAuthentication auth) {
		MemberResponse foundMember = memberService.findByUsername(username);

		model.addAttribute("auth", auth);
		model.addAttribute("thumbnails", postGiver.findPostThumbnailsByMemberId(foundMember.id()));
		model.addAttribute("profileInfo", profileService.findProfileInfoByUsername(username));

		return "personal-page";
	}

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
	public String signInPage(@AuthenticationPrincipal JwtAuthentication auth) {
		if (auth == null) {
			return "signin";
		}

		return "redirect:/";
	}

	@GetMapping("/signup")
	public String renderSignUpPage(@AuthenticationPrincipal JwtAuthentication auth) {
		if (auth == null) {
			return "signup";
		}

		return "redirect:/";
	}
}
