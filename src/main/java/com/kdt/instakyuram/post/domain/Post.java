package com.kdt.instakyuram.post.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.DynamicUpdate;

import com.kdt.instakyuram.common.BaseEntity;
import com.kdt.instakyuram.member.domain.Member;

import lombok.Builder;

@DynamicUpdate
@Entity
public class Post extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@Size(min = 5, max = 500, message = "5글자 ~ 500글자 까지만 입력하실 수 있습니다.")
	private String content;

	@ManyToOne
	@JoinColumn(name = "member_id")
	private Member member;

	protected Post() {
	}

	@Builder
	public Post(Long id, String content, Member member) {
		this.id = id;
		this.content = content;
		this.member = member;
	}

	public Long getId() {
		return id;
	}

	public String getContent() {
		return content;
	}

	public Member getMember() {
		return member;
	}

	public void updateContent(String content) {
		this.content = content;
	}
}
