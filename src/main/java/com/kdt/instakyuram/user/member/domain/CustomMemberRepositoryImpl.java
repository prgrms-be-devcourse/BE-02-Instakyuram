package com.kdt.instakyuram.user.member.domain;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.kdt.instakyuram.user.member.dto.MemberSearchDto;

@Repository
public class CustomMemberRepositoryImpl implements CustomMemberRepository {
	private final EntityManager entityManager;

	private final NamedParameterJdbcTemplate jdbcTemplate;

	public CustomMemberRepositoryImpl(EntityManager entityManager, NamedParameterJdbcTemplate jdbcTemplate) {
		this.entityManager = entityManager;
		this.jdbcTemplate = jdbcTemplate;
	}

	/**
	 * note : 사용자가 검색을 한다.
	 *    1. 검색 조건이 있을 수도 있고 없을 수도 있다.
	 *    	- 단, condition
	 *    2. : order by ${정렬의 주체}
	 *    3. 정렬의 주체가 있어야 asc, desc 을 할 수 있다.
	 *    jdbc template이 더 깔끔하다.
	 */
	@Override
	public Page<Member> findByAllForPaging(Long auId, MemberSearchDto memberSearchDto, Pageable pageable) {
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		//note :Criteria 생성, 반환 타입 지정 -> entityManger.createQuery 파라미터로 들어감.
		CriteriaQuery<Member> query = builder.createQuery(Member.class);
		//note : from 절
		Root<Member> member = query.from(Member.class); // 애는 모지

		CriteriaQuery<Member> dynamic = Optional.ofNullable(memberSearchDto.memberCondition())
			.map(condition -> {

				switch (condition) {
					case USERNAME -> {
						return Optional.ofNullable(memberSearchDto.order())
							.map(order -> {
								if (order == MemberSearchDto.SearchOrder.ASC) {
									return query.orderBy(
										builder.asc(member.get(MemberSearchDto.MemberCondition.USERNAME.getValue())));
								}

								return query.orderBy(
									builder.desc(member.get(MemberSearchDto.MemberCondition.USERNAME.getValue())));
							})
							.orElseGet(
								() -> query.orderBy(
									builder.desc(member.get(MemberSearchDto.MemberCondition.USERNAME.getValue()))));
					}
					case NAME -> {
						return Optional.ofNullable(memberSearchDto.order())
							.map(order -> {
								if (order == MemberSearchDto.SearchOrder.ASC) {
									return query.orderBy(
										builder.asc(member.get(MemberSearchDto.MemberCondition.NAME.getValue())));
								}

								return query.orderBy(
									builder.desc(member.get(MemberSearchDto.MemberCondition.NAME.getValue())));
							})
							.orElseGet(() -> query.orderBy(
								builder.desc(member.get(MemberSearchDto.MemberCondition.NAME.getValue()))));
					}
					case UPDATED_AT -> {
						return Optional.ofNullable(memberSearchDto.order())
							.map(order -> {
								if (order == MemberSearchDto.SearchOrder.ASC) {
									return query.orderBy(
										builder.asc(member.get(MemberSearchDto.MemberCondition.UPDATED_AT.getValue())));
								}

								return query.orderBy(
									builder.desc(member.get(MemberSearchDto.MemberCondition.UPDATED_AT.getValue())));
							})
							.orElseGet(
								() -> query.orderBy(
									builder.desc(member.get(MemberSearchDto.MemberCondition.UPDATED_AT.getValue()))));
					}
					default -> {
						return null;
					}
				}
			}).orElseGet(() -> null);

		List<Member> members = entityManager.createQuery(dynamic.where().orderBy())
			.setFirstResult((int)pageable.getOffset())
			.setMaxResults(pageable.getPageSize())
			.getResultList();

		CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);
		Root<Member> root = countQuery.from(Member.class);

		countQuery.select(builder.count(root));
		Long count = entityManager.createQuery(countQuery).getSingleResult();

		return new PageImpl<>(members, pageable, count);
	}
}
