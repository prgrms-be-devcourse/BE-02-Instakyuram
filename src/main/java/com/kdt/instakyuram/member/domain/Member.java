package com.kdt.instakyuram.member.domain;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Member {
	@Id
	private Long id;

	private String name;

	private String userName;

	private String password;

	private String phoneNunmber;
}
