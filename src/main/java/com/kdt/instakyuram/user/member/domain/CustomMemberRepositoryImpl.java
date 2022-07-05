package com.kdt.instakyuram.user.member.domain;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.stereotype.Repository;

@Repository
public class CustomMemberRepositoryImpl implements CustomMemberRepository {
	private final EntityManager entityManager;

	public CustomMemberRepositoryImpl(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public Page<Member> findAllExcludeAuth(Long auId, Pageable pageable) {
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Member> query = builder.createQuery(Member.class);
		Root<Member> member = query.from(Member.class);
		List<Order> orderBys = QueryUtils.toOrders(pageable.getSort(), member, builder);
		List<Member> memberQuery = entityManager.createQuery(
				query.select(member)
					.where(builder.notEqual(member.get("id"), auId))
					.orderBy(orderBys)
			).setFirstResult((int)pageable.getOffset())
			.setMaxResults(pageable.getPageSize())
			.getResultList();

		CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);
		countQuery.select(builder.count(countQuery.from(Member.class)));
		Long count = entityManager.createQuery(countQuery).getSingleResult();

		return new PageImpl<>(memberQuery, pageable, count);
	}
}
