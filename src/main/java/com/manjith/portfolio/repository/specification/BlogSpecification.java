package com.manjith.portfolio.repository.specification;

import com.manjith.portfolio.entity.Blog;
import com.manjith.portfolio.entity.BlogStatus;
import com.manjith.portfolio.entity.BlogTag;
import com.manjith.portfolio.entity.Category;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public final class BlogSpecification {

    private BlogSpecification() {
    }

    public static Specification<Blog> withFilters(String categorySlug, String tagSlug, String search, BlogStatus status) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(categorySlug)) {
                Join<Blog, Category> categoryJoin = root.join("category", JoinType.INNER);
                predicates.add(cb.equal(categoryJoin.get("slug"), categorySlug));
            }

            if (StringUtils.hasText(tagSlug)) {
                Join<Blog, BlogTag> tagJoin = root.join("tags", JoinType.INNER);
                predicates.add(cb.equal(tagJoin.get("slug"), tagSlug));
                if (query != null) {
                    query.distinct(true); // the tags join can multiply rows
                }
            }

            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            if (StringUtils.hasText(search)) {
                String pattern = "%" + search.trim().toLowerCase() + "%";
                Predicate titleMatch = cb.like(cb.lower(root.get("title")), pattern);
                Predicate excerptMatch = cb.like(cb.lower(root.get("excerpt")), pattern);
                predicates.add(cb.or(titleMatch, excerptMatch));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
