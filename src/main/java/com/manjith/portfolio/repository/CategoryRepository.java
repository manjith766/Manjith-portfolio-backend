package com.manjith.portfolio.repository;

import com.manjith.portfolio.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
