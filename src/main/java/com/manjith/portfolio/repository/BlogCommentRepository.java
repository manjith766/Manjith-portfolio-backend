package com.manjith.portfolio.repository;

import com.manjith.portfolio.entity.BlogComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BlogCommentRepository extends JpaRepository<BlogComment, Long> {

    List<BlogComment> findAllByBlog_IdAndIsApprovedTrueOrderByCreatedAtAsc(Long blogId);

    Page<BlogComment> findAllByIsApprovedOrderByCreatedAtAsc(boolean isApproved, Pageable pageable);
}
