package com.kdt.instakyuram.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kdt.instakyuram.auth.domain.Token;

public interface TokenRepository extends JpaRepository<Token, String> {
}
