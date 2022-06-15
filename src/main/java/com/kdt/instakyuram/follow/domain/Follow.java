package com.kdt.instakyuram.follow.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.kdt.instakyuram.member.domain.Member;

@Entity
public class Follow {
	@Id
	private Long id;

	@ManyToOne
	@JoinColumn(name = "member_id", referencedColumnName = "id")
	private Member memberId;

	@ManyToOne
	@JoinColumn(name = "target_id", referencedColumnName = "id")
	private Member targetId;
}
