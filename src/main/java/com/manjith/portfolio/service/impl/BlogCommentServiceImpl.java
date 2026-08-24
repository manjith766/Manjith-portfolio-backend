package com.manjith.portfolio.service.impl;

import com.manjith.portfolio.constants.AppConstants;
import com.manjith.portfolio.dto.request.BlogCommentRequestDTO;
import com.manjith.portfolio.dto.response.AdminBlogCommentResponseDTO;
import com.manjith.portfolio.dto.response.BlogCommentResponseDTO;
import com.manjith.portfolio.dto.response.PageResponseDTO;
import com.manjith.portfolio.entity.Blog;
import com.manjith.portfolio.entity.BlogComment;
import com.manjith.portfolio.entity.BlogStatus;
import com.manjith.portfolio.exception.ErrorCode;
import com.manjith.portfolio.exception.ResourceNotFoundException;
import com.manjith.portfolio.mapper.BlogCommentMapper;
import com.manjith.portfolio.repository.BlogCommentRepository;
import com.manjith.portfolio.repository.BlogRepository;
import com.manjith.portfolio.service.BlogCommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Comments are a distinct resource with a distinct lifecycle (submit ->
 * moderate -> approve/delete) from the Blog post itself, so this is its
 * own service rather than folded into BlogServiceImpl — same reasoning
 * that gave Auth its own service separate from User.
 *
 * QUERY COUNT:
 *   getApprovedComments(slug) -> 1 SELECT to resolve blog by slug + 1
 *                                 SELECT for its approved comments.
 *   submitComment(...)        -> 1 SELECT (resolve blog) + 1 INSERT.
 *   getPendingComments(...)   -> 1 paginated SELECT, no joins needed
 *                                 beyond the blog title (fetched via the
 *                                 comment's already-loaded blog reference
 *                                 — see BlogCommentMapper).
 *   approveComment / deleteComment -> 1 SELECT + 1 UPDATE/DELETE each.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BlogCommentServiceImpl implements BlogCommentService {

    private final BlogCommentRepository blogCommentRepository;
    private final BlogRepository blogRepository;
    private final BlogCommentMapper blogCommentMapper;

    @Override
    @Transactional(readOnly = true)
    public List<BlogCommentResponseDTO> getApprovedComments(String blogSlug) {
        log.info("API entry: getApprovedComments blogSlug={}", blogSlug);

        Blog blog = resolvePublishedBlog(blogSlug);
        List<BlogComment> comments = blogCommentRepository.findAllByBlog_IdAndIsApprovedTrueOrderByCreatedAtAsc(blog.getId());

        List<BlogCommentResponseDTO> response = comments.stream()
                .map(blogCommentMapper::toResponseDTO)
                .collect(Collectors.toList());

        log.info("API exit: getApprovedComments returning {} comments for blogSlug={}", response.size(), blogSlug);
        return response;
    }

    @Override
    @Transactional
    public BlogCommentResponseDTO submitComment(String blogSlug, BlogCommentRequestDTO requestDTO) {
        log.info("API entry: submitComment blogSlug={}", blogSlug);

        Blog blog = resolvePublishedBlog(blogSlug);

        BlogComment comment = BlogComment.builder()
                .blog(blog)
                .name(requestDTO.getName())
                .email(requestDTO.getEmail())
                .comment(requestDTO.getComment())
                .isApproved(false) // moderation queue — never auto-published
                .build();

        BlogComment saved = blogCommentRepository.save(comment);
        log.debug("Persisted comment id={} for blog id={} (pending approval)", saved.getId(), blog.getId());

        log.info("API exit: submitComment created id={} (pending moderation)", saved.getId());
        return blogCommentMapper.toResponseDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<AdminBlogCommentResponseDTO> getPendingComments(int page, int size) {
        log.info("API entry: getPendingComments page={}, size={}", page, size);

        int safePage = Math.max(page, AppConstants.DEFAULT_PAGE_NUMBER);
        int safeSize = size <= 0 ? AppConstants.DEFAULT_PAGE_SIZE : Math.min(size, AppConstants.MAX_PAGE_SIZE);
        Pageable pageable = PageRequest.of(safePage, safeSize, Sort.by(Sort.Direction.ASC, "createdAt"));

        Page<BlogComment> commentPage = blogCommentRepository.findAllByIsApprovedOrderByCreatedAtAsc(false, pageable);
        Page<AdminBlogCommentResponseDTO> dtoPage = commentPage.map(blogCommentMapper::toAdminResponseDTO);

        log.info("API exit: getPendingComments returning {} of {} total", dtoPage.getNumberOfElements(), dtoPage.getTotalElements());
        return PageResponseDTO.of(dtoPage);
    }

    @Override
    @Transactional
    public void approveComment(Long commentId) {
        log.info("API entry: approveComment id={}", commentId);

        BlogComment comment = blogCommentRepository.findById(commentId)
                .orElseThrow(() -> {
                    log.warn("Comment not found id={}", commentId);
                    return new ResourceNotFoundException(ErrorCode.COMMENT_NOT_FOUND, "No comment found with id: " + commentId);
                });

        comment.setIsApproved(true);
        blogCommentRepository.save(comment);

        log.info("API exit: approveComment approved id={}", commentId);
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId) {
        log.info("API entry: deleteComment id={}", commentId);

        if (!blogCommentRepository.existsById(commentId)) {
            log.warn("Comment not found for delete, id={}", commentId);
            throw new ResourceNotFoundException(ErrorCode.COMMENT_NOT_FOUND, "No comment found with id: " + commentId);
        }

        blogCommentRepository.deleteById(commentId);
        log.info("API exit: deleteComment deleted id={}", commentId);
    }

    /**
     * Comments can only be read or submitted against a PUBLISHED post —
     * this prevents both commenting on drafts and using slug guessing to
     * discover that an unpublished post exists.
     */
    private Blog resolvePublishedBlog(String slug) {
        return blogRepository.findBySlug(slug)
                .filter(b -> b.getStatus() == BlogStatus.PUBLISHED)
                .orElseThrow(() -> {
                    log.warn("Published blog not found for slug={}", slug);
                    return new ResourceNotFoundException(ErrorCode.BLOG_NOT_FOUND, "No published blog found with slug: " + slug);
                });
    }
}
