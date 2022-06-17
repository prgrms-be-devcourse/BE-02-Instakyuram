package com.kdt.instakyuram.member.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

import lombok.Builder;

@Entity
public class Member {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Size(min = 6, max = 24)
	private String username;

	@Size(min = 6, max = 16)
	private String name;

	private String password;

	@Size(max = 11)
	private String phoneNumber;

	@Email
	private String email;

	protected Member() {
	}

	@Builder
	public Member(Long id, String username, String name, String password, String phoneNumber, String email) {
		this.id = id;
		this.username = username;
		this.name = name;
		this.password = password;
		this.phoneNumber = phoneNumber;
		this.email = email;
	}

	public Member(String username, String password, String name, String phoneNumber, String email) {
		this.name = name;
		this.username = username;
		this.password = password;
		this.phoneNumber = phoneNumber;
		this.email = email;
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public String getEmail() {
		return email;
	}
}
