package com.kdt.instakyuram.token.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kdt.instakyuram.token.domain.Token;

public interface TokenRepository extends JpaRepository<Token, String> {
}
