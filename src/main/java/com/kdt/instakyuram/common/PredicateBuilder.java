package com.kdt.instakyuram.common;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class PredicateBuilder {

	private List<Predicate> predicates;
	private CriteriaBuilder cb;
	private Root<? extends BaseEntity> query;

	private PredicateBuilder(List<Predicate> predicates, CriteriaBuilder cb,
		Root<? extends BaseEntity> query) {
		this.predicates = predicates;
		this.cb = cb;
		this.query = query;
	}

	public static PredicateBuilder predicateBuilder(CriteriaBuilder cb, Root<? extends BaseEntity> query) {
		return new PredicateBuilder(new ArrayList<>(), cb, query);
	}

	public PredicateBuilder dateEqual(String fieldName, LocalDateTime start, LocalDateTime end) {
		if (start != null && end != null) {
			this.predicates.add(cb.between(query.get(fieldName), start, end));
		} else if (start != null) {
			this.predicates.add(cb.greaterThanOrEqualTo(query.get(fieldName), start));
		} else if (end != null) {
			this.predicates.add(cb.lessThanOrEqualTo(query.get(fieldName), end));
		}

		return this;
	}

	public PredicateBuilder eq(String fieldName, Long data) {
		if (data != null) {
			this.predicates.add(cb.equal(query.get(fieldName), data));
		}

		return this;
	}

	public PredicateBuilder eq(String fieldName, String data) {
		if (data != null) {
			this.predicates.add(cb.equal(query.get(fieldName), data));
		}

		return this;
	}

	public PredicateBuilder startingWith(String fieldName, String content) {
		if (content != null) {
			this.predicates.add(cb.like(query.get(fieldName), "%" + content));
		}

		return this;
	}

	public PredicateBuilder endingWith(String fieldName, String content) {
		if (content != null) {
			this.predicates.add(cb.like(query.get(fieldName), content + "%"));
		}

		return this;
	}

	public PredicateBuilder includingWith(String fieldName, String content) {
		if (content != null) {
			this.predicates.add(cb.like(query.get(fieldName), "%" + content + "%"));
		}

		return this;
	}

	public PredicateBuilder lessThan(String fieldName, Long data) {
		if (data != null) {
			this.predicates.add(cb.lessThan(query.get(fieldName), data));
		}

		return this;
	}

	public PredicateBuilder lessThan( String fieldName, LocalDateTime data) {
		if (data != null) {
			this.predicates.add(cb.lessThanOrEqualTo(query.get(fieldName), data));
		}

		return this;
	}

	public PredicateBuilder lessThanOrEqualTo(String fieldName, Long data) {
		if (data != null) {
			this.predicates.add(cb.lessThanOrEqualTo(query.get(fieldName), data));
		}

		return this;
	}

	public PredicateBuilder lessThanOrEqualTo( String fieldName, LocalDateTime data) {
		if (data != null) {
			this.predicates.add(cb.lessThanOrEqualTo(query.get(fieldName), data));
		}

		return this;
	}

	public PredicateBuilder greaterThan(String fieldName, Long data) {
		if (data != null) {
			this.predicates.add(cb.greaterThan(query.get(fieldName), data));
		}

		return this;
	}

	public PredicateBuilder greaterThan(String fieldName, LocalDateTime data) {
		if (data != null) {
			this.predicates.add(cb.greaterThan(query.get(fieldName), data));
		}

		return this;
	}

	public PredicateBuilder greaterThanOrEqualToterThan(String fieldName, Long data) {
		if (data != null) {
			this.predicates.add(cb.greaterThanOrEqualTo(query.get(fieldName), data));
		}

		return this;
	}

	public PredicateBuilder greaterThanOrEqualToterThan(String fieldName, LocalDateTime data) {
		if (data != null) {
			this.predicates.add(cb.greaterThanOrEqualTo(query.get(fieldName), data));
		}

		return this;
	}

	public PredicateBuilder or(CriteriaBuilder cb, Predicate... predicateArr) {
		List<Predicate> predicates = this.removeNull(predicateArr);

		if(predicates.size() > 0) {
			this.predicates.add(cb.or(predicates.toArray(new Predicate[0])));
		}

		return this;
	}

	public PredicateBuilder and(CriteriaBuilder cb, Predicate... predicateArr) {
		List<Predicate> predicates = this.removeNull(predicateArr);

		if(predicates.size() > 0) {
			this.predicates.add(cb.and(predicates.toArray(new Predicate[0])));
		}

		return this;
	}

	public Predicate singleBuild() {
		if(this.predicates.isEmpty()){
			return null;
		}

		return this.predicates.get(0);
	}

	public Predicate[] build() {
		List<Predicate> predicates = this.removeNull(this.predicates);

		return predicates.toArray(new Predicate[0]);
	}

	private List<Predicate> removeNull(List<Predicate> predicates) {
		return predicates.stream().filter(p -> p != null)
			.toList();
	}

	private List<Predicate> removeNull(Predicate[] predicates) {
		return Arrays.stream(predicates).filter(p -> p != null)
			.toList();
	}

}
