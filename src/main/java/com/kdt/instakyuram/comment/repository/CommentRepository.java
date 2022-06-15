package com.kdt.instakyuram.comment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kdt.instakyuram.comment.domain.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
