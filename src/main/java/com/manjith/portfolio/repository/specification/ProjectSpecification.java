package com.manjith.portfolio.repository.specification;

import com.manjith.portfolio.entity.Category;
import com.manjith.portfolio.entity.Project;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public final class ProjectSpecification {

    private ProjectSpecification() {
    }

    public static Specification<Project> withFilters(String categorySlug, Boolean featured, String search) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(categorySlug)) {
                Join<Project, Category> categoryJoin = root.join("category", JoinType.INNER);
                predicates.add(cb.equal(categoryJoin.get("slug"), categorySlug));
            }

            if (featured != null) {
                predicates.add(cb.equal(root.get("isFeatured"), featured));
            }

            if (StringUtils.hasText(search)) {
                String pattern = "%" + search.trim().toLowerCase() + "%";
                Predicate titleMatch = cb.like(cb.lower(root.get("title")), pattern);
                Predicate descriptionMatch = cb.like(cb.lower(root.get("description")), pattern);
                predicates.add(cb.or(titleMatch, descriptionMatch));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
