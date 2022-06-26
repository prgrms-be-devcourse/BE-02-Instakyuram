package com.kdt.instakyuram.comment.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.kdt.instakyuram.exception.ErrorCodeV2;
import com.kdt.instakyuram.exception.ServiceNotFoundException;

@ActiveProfiles("test")
@SpringBootTest
class CommentServiceTest {

	@Autowired
	CommentService commentService;

	@Test
	@Transactional
	void name() {
		throw new ServiceNotFoundException(ErrorCodeV2.COMMENT_NOT_FOUND, 1L);
	}
}