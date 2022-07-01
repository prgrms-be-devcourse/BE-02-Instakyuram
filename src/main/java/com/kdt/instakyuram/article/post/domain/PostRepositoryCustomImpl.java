package com.kdt.instakyuram.article.post.domain;

import static com.kdt.instakyuram.common.PredicateBuilder.predicateBuilder;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.kdt.instakyuram.user.member.domain.Member;

@Repository
public class PostRepositoryCustomImpl implements PostRepositoryCustom {

	private final EntityManager entityManager;

	public PostRepositoryCustomImpl(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@Override
	public List<Post> search(List<Member> members, PostCondition postCondition) {

		/*
		// TODO : 처음에 했던 방법
		Predicate p1 = cb.greaterThanOrEqualTo(query.get("createdAt"), postCondition.getStartDate());
		Predicate p2 = cb.lessThanOrEqualTo(query.get("createdAt"), postCondition.getEndDate());
		Predicate p3 = cb.lessThan(query.get("id"), postCondition.getLastId());
		Predicate p4 = cb.like(query.get("content"), "%" + postCondition.getContent());

		Predicate predicate = cb.or(
			cb.and(p1, p2),
			p3,
			p4
		);

		cq.select(query)
			.where(predicate);
		*/

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Post> cq = cb.createQuery(Post.class);
		Root<Post> query = cq.from(Post.class);

		cq.select(query)
			.where(
				predicateBuilder(cb, query)
					.greaterThanOrEqualToterThan("id", postCondition.getLastId())
					.dateEqual("createdAt", postCondition.getStartDate(), postCondition.getEndDate())
					.or(
						cb,
						predicateBuilder(cb,query)
							.includingWith("content", postCondition.getContent())
							.singleBuild(),
						predicateBuilder(cb,query)
							.greaterThan("updatedAt", postCondition.getUpdateDate())
							.singleBuild()
					)
					.build()
			)
			.orderBy(cb.desc(query.get("id")));

		return entityManager.createQuery(cq)
			.setMaxResults(postCondition.getSize())
			.getResultList();
	}

}
