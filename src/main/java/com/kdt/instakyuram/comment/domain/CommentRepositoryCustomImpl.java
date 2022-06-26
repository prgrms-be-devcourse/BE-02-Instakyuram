package com.kdt.instakyuram.comment.domain;

import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.stereotype.Repository;

import com.kdt.instakyuram.comment.dto.CommentFindAllResponse;

@Repository
public class CommentRepositoryCustomImpl implements CommentRepositoryCustom {

	private final EntityManager em;

	public CommentRepositoryCustomImpl(EntityManager em) {
		this.em = em;
	}

	// TODO Criteria 로 변환 필요
	@Override
	public List<CommentFindAllResponse> findAllByPostIdAndMemberId(Long postId, Long memberId) {
		String sql = "SELECT new com.kdt.instakyuram.comment.dto.CommentFindAllResponse(c.id, c.post.id, c.content, c.member.id, m.username, count(cl.id), case when cl.member.id = :memberId then true else false end)"
			+ " FROM Comment c"
			+ " INNER JOIN Member m ON c.member.id = m.id"
			+ " LEFT OUTER JOIN CommentLike cl ON c.id = cl.comment.id"
			+ " WHERE c.post.id = :postId"
			+ " GROUP BY c.id, c.post.id, c.content, c.member.id, m.username, case when cl.member.id = :memberId then true else false end";

		return em.createQuery(sql, CommentFindAllResponse.class)
			.setParameter("postId", postId)
			.setParameter("memberId", memberId)
			.getResultList();
	}
}
