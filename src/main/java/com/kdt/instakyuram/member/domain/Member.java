package com.kdt.instakyuram.member.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.Builder;

@Entity
public class Member {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Size(min = 6, max = 24)
	@Column(unique = true)
	@Pattern(regexp = "^[a-z0-9]*$")
	@NotBlank
	private String username;

	@Size(min = 2, max = 16)
	@NotBlank
	private String name;

	@NotBlank
	private String password;

	@Size(min = 11, max = 11)
	@NotBlank
	private String phoneNumber;

	@Email
	@Column(unique = true)
	@NotBlank
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