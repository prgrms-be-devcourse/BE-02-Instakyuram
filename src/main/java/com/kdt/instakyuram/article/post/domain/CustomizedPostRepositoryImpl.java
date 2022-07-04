package com.kdt.instakyuram.article.post.domain;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.kdt.instakyuram.common.PageDto;
import com.kdt.instakyuram.user.member.domain.Member;

@Repository
public class CustomizedPostRepositoryImpl implements CustomizedPostRepository {

	private final EntityManager entityManager;

	public CustomizedPostRepositoryImpl(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@Override
	public List<Post> findAllCursorPaging(List<Member> members, PageDto.PostFindAllPageRequest pageRequest) {
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Post> query = builder.createQuery(Post.class);
		Root<Post> post = query.from(Post.class);

		PostPagingCriteria pagingCriteria = pageRequest.getPostPagingCriteria();

		Predicate memberIn = post.get("member").in(members);
		Predicate createAtGt = Optional
			.ofNullable(pagingCriteria.getStartDate())
			.map(date -> builder.greaterThanOrEqualTo(post.get("createdAt"), date))
			.orElse(null);
		Predicate createAtLe = Optional
			.ofNullable(pagingCriteria.getEndDate())
			.map(date -> builder.lessThanOrEqualTo(post.get("createdAt"), date))
			.orElse(null);
		Predicate lastIdLe = Optional.ofNullable(pagingCriteria.getId())
			.map(data -> builder.lessThan(post.get("id"), data))
			.orElse(null);

		Predicate predicate = builder.and
			(
				Stream.of(memberIn, createAtGt, createAtLe, lastIdLe)
					.filter(Objects::nonNull)
					.toArray(Predicate[]::new)
			);

		query.select(post)
			.where(predicate)
			.orderBy(
				builder.desc(post.get("createdAt")),
				builder.desc(post.get("id"))
			);

		return entityManager.createQuery(query)
			.setMaxResults(pageRequest.getSize())
			.getResultList();
	}

	@Override
	public boolean isFindAllCursorHasNext(List<Member> members, Long id, LocalDateTime begin, LocalDateTime end) {
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Post> query = builder.createQuery(Post.class);
		Root<Post> post = query.from(Post.class);

		Predicate memberIn = post.get("member").in(members);
		Predicate createAtGt = Optional
			.ofNullable(begin)
			.map(date -> builder.greaterThanOrEqualTo(post.get("createdAt"), date))
			.orElse(null);
		Predicate createAtLe = Optional
			.ofNullable(end)
			.map(date -> builder.lessThanOrEqualTo(post.get("createdAt"), date))
			.orElse(null);
		Predicate lastIdLe = Optional
			.ofNullable(id)
			.map(data -> builder.lessThan(post.get("id"), data))
			.orElse(null);

		Predicate predicate = builder.and
			(
				Stream.of(memberIn, createAtGt, createAtLe, lastIdLe)
					.filter(Objects::nonNull)
					.toArray(Predicate[]::new)
			);

		query.select(post)
			.where(predicate);

		return entityManager.createQuery(query)
			.setMaxResults(1)
			.getResultList().size() == 1;
	}

	public List<Post> findAllByUsernameCursorPaging(String username, PageDto.PostCursorPageRequest pageRequest) {
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Post> query = builder.createQuery(Post.class);
		Root<Post> post = query.from(Post.class);
		post.fetch("member", JoinType.LEFT);

		PostPagingCursor cursor = pageRequest.getCursor();

		Predicate usernamePredicate = Optional.ofNullable(username)
			.map(value -> builder.equal(post.get("member").get("username"), value))
			.orElse(null);
		Predicate cursorPredicate = Optional.ofNullable(cursor)
			.map((c) -> builder.or(
				builder.lessThan(post.get("updatedAt"), c.getUpdatedAt()),
				builder.and(
					builder.equal(post.get("updatedAt"), c.getUpdatedAt()),
					builder.lessThan(post.get("id"), c.getId())
				))
			)
			.orElse(null);

		query.select(post)
			.where(Stream.of(usernamePredicate, cursorPredicate)
				.filter(Objects::nonNull)
				.toArray(Predicate[]::new)
			).orderBy(builder.desc(post.get("updatedAt")), builder.desc(post.get("id")));

		return entityManager.createQuery(query).setMaxResults(pageRequest.getSize()).getResultList();
	}

}

