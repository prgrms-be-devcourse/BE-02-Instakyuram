package com.kdt.instakyuram.article.post.domain;

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

import com.kdt.instakyuram.common.PageDto;

public class CustomizedPostRepositoryImpl implements CustomizedPostRepository {

	private final EntityManager entityManager;

	public CustomizedPostRepositoryImpl(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public List<Post> findAllByUsernameCursorPaging(String username, PageDto.PostCursorPageRequest pageRequest) {
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Post> query = builder.createQuery(Post.class);
		Root<Post> p = query.from(Post.class);
		p.fetch("member", JoinType.LEFT);

		PostPagingCursor cursor = pageRequest.getCursor();

		Predicate usernamePredicate = Optional.ofNullable(username)
			.map(value -> builder.equal(p.get("member").get("username"), value))
			.orElse(null);
		Predicate cursorPredicate = Optional.ofNullable(cursor)
			.map((c) -> builder.or(
				builder.lessThan(p.get("updatedAt"), c.getUpdatedAt()),
				builder.and(
					builder.equal(p.get("updatedAt"), c.getUpdatedAt()),
					builder.lessThan(p.get("id"), c.getId())
				))
			)
			.orElse(null);

		query.select(p)
			.where(Stream.of(usernamePredicate, cursorPredicate)
				.filter(Objects::nonNull)
				.toArray(Predicate[]::new)
			).orderBy(builder.desc(p.get("updatedAt")), builder.desc(p.get("id")));

		return entityManager.createQuery(query).setMaxResults(pageRequest.getSize()).getResultList();
	}

}

