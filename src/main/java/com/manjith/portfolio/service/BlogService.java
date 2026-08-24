package com.manjith.portfolio.service;

import com.manjith.portfolio.dto.request.BlogRequestDTO;
import com.manjith.portfolio.dto.response.BlogResponseDTO;
import com.manjith.portfolio.dto.response.BlogSummaryResponseDTO;
import com.manjith.portfolio.dto.response.PageResponseDTO;
import com.manjith.portfolio.entity.BlogStatus;

public interface BlogService {

    // ---- Public (always PUBLISHED-only, enforced in the impl, not the caller) ----
    PageResponseDTO<BlogSummaryResponseDTO> getPublishedBlogs(
            String categorySlug, String tagSlug, String search,
            int page, int size, String sortBy, String sortDirection);

    BlogResponseDTO getPublishedBlogBySlug(String slug);

    void incrementViews(String slug);

    void incrementLikes(String slug);

    // ---- Admin (any status, including drafts) ----
    PageResponseDTO<BlogSummaryResponseDTO> getAllBlogsForAdmin(
            BlogStatus status, int page, int size, String sortBy, String sortDirection);

    BlogResponseDTO getBlogByIdForAdmin(Long id);

    BlogResponseDTO createBlog(BlogRequestDTO requestDTO, Long authorUserId);

    BlogResponseDTO updateBlog(Long id, BlogRequestDTO requestDTO);

    void deleteBlog(Long id);
}
