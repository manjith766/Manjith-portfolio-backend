package com.manjith.portfolio.service;

import com.manjith.portfolio.dto.request.BlogCommentRequestDTO;
import com.manjith.portfolio.dto.response.AdminBlogCommentResponseDTO;
import com.manjith.portfolio.dto.response.BlogCommentResponseDTO;
import com.manjith.portfolio.dto.response.PageResponseDTO;

import java.util.List;

public interface BlogCommentService {

    List<BlogCommentResponseDTO> getApprovedComments(String blogSlug);

    BlogCommentResponseDTO submitComment(String blogSlug, BlogCommentRequestDTO requestDTO);

    PageResponseDTO<AdminBlogCommentResponseDTO> getPendingComments(int page, int size);

    void approveComment(Long commentId);

    void deleteComment(Long commentId);
}
