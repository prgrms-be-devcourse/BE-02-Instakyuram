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

