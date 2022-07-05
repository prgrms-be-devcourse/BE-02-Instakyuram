package com.kdt.instakyuram.article.comment.domain;

import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.stereotype.Repository;

import com.kdt.instakyuram.article.comment.dto.CommentFindAllResponse;

@Repository
public class CommentRepositoryCustomImpl implements CommentRepositoryCustom {

	private final EntityManager em;

	public CommentRepositoryCustomImpl(EntityManager em) {
		this.em = em;
	}

	@Override
	public List<CommentFindAllResponse> findAllByPostIdAndMemberId(Long postId, Long memberId, Long id, Integer limit) {
		String sql =
			"SELECT new com.kdt.instakyuram.article.comment.dto.CommentFindAllResponse(c.id, c.post.id, c.content, c.createdAt, c.member.id, m.username, m.profileImageName, count(cl.id), case when cl.member.id = :memberId then true else false end, case when c.createdAt <> c.updatedAt then true else false end)"
				+ " FROM Comment c"
				+ " INNER JOIN Member m ON c.member.id = m.id"
				+ " LEFT OUTER JOIN CommentLike cl ON c.id = cl.comment.id"
				+ " WHERE c.post.id = :postId"
				+ " AND c.id > :id"
				+ " GROUP BY c.id, c.post.id, c.content, c.createdAt, c.member.id, m.username, m.profileImageName, case when cl.member.id = :memberId then true else false end, case when c.createdAt <> c.updatedAt then true else false end";

		return em.createQuery(sql, CommentFindAllResponse.class)
			.setParameter("postId", postId)
			.setParameter("memberId", memberId)
			.setParameter("id", id)
			.setMaxResults(limit)
			.getResultList();
	}
}
