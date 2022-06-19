package com.kdt.instakyuram.member.controller;

import javax.validation.Valid;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.kdt.instakyuram.common.PageDto;
import com.kdt.instakyuram.member.service.MemberService;

@RequestMapping("/members")
@Controller
public class MemberController {
	private final MemberService memberService;

	public MemberController(MemberService memberService) {
		this.memberService = memberService;
	}

	@GetMapping
	public ModelAndView getMembers(@ModelAttribute @Valid PageDto.Request pagingDto) {
		Pageable requestPage = pagingDto.getPageable(Sort.by("id").descending());

		return new ModelAndView("member/member-list")
			.addObject(memberService.findAll(requestPage));
	}
}
