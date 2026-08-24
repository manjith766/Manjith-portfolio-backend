package com.manjith.portfolio.service.impl;

import com.manjith.portfolio.constants.AppConstants;
import com.manjith.portfolio.dto.request.BlogRequestDTO;
import com.manjith.portfolio.dto.response.BlogResponseDTO;
import com.manjith.portfolio.dto.response.BlogSummaryResponseDTO;
import com.manjith.portfolio.dto.response.PageResponseDTO;
import com.manjith.portfolio.entity.Blog;
import com.manjith.portfolio.entity.BlogStatus;
import com.manjith.portfolio.entity.BlogTag;
import com.manjith.portfolio.entity.Category;
import com.manjith.portfolio.entity.CategoryType;
import com.manjith.portfolio.entity.User;
import com.manjith.portfolio.exception.BusinessValidationException;
import com.manjith.portfolio.exception.DuplicateResourceException;
import com.manjith.portfolio.exception.ErrorCode;
import com.manjith.portfolio.exception.ResourceNotFoundException;
import com.manjith.portfolio.mapper.BlogMapper;
import com.manjith.portfolio.repository.BlogRepository;
import com.manjith.portfolio.repository.BlogTagRepository;
import com.manjith.portfolio.repository.CategoryRepository;
import com.manjith.portfolio.repository.UserRepository;
import com.manjith.portfolio.repository.specification.BlogSpecification;
import com.manjith.portfolio.service.BlogService;
import com.manjith.portfolio.util.SlugUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * QUERY COUNT PER ENDPOINT (same pattern as ProjectServiceImpl):
 *   getPublishedBlogs(...)   -> 1 (page + category/author via @EntityGraph)
 *                                + at most 1 batched query for tags across
 *                                the page (hibernate.default_batch_fetch_size).
 *   getPublishedBlogBySlug()-> 1 (post + category/author) + 1 for tags +
 *                                1 for comments = 3 fixed for one post.
 *   incrementViews/Likes()  -> 1 atomic UPDATE each, no read first.
 *   createBlog(...)         -> 1 SELECT (slug uniqueness) + 1 SELECT
 *                                (category, if given) + 1 SELECT (author)
 *                                + up to N SELECTs for tag resolution
 *                                (find-or-create per tag — see
 *                                resolveTags Javadoc for why this isn't
 *                                batched) + 1 INSERT.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BlogServiceImpl implements BlogService {

    private static final int WORDS_PER_MINUTE = 200;

    private final BlogRepository blogRepository;
    private final CategoryRepository categoryRepository;
    private final BlogTagRepository blogTagRepository;
    private final UserRepository userRepository;
    private final BlogMapper blogMapper;

    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<BlogSummaryResponseDTO> getPublishedBlogs(
            String categorySlug, String tagSlug, String search,
            int page, int size, String sortBy, String sortDirection) {

        log.info("API entry: getPublishedBlogs categorySlug={}, tagSlug={}, search={}", categorySlug, tagSlug, search);

        Pageable pageable = buildPageable(page, size, sortBy, sortDirection, "publishedAt");
        // status is hardcoded to PUBLISHED here — never derived from a
        // caller-supplied parameter, so public callers can never see drafts.
        Specification<Blog> spec = BlogSpecification.withFilters(categorySlug, tagSlug, search, BlogStatus.PUBLISHED);

        Page<Blog> blogPage = blogRepository.findAll(spec, pageable);
        Page<BlogSummaryResponseDTO> dtoPage = blogPage.map(blogMapper::toSummaryDTO);

        log.info("API exit: getPublishedBlogs returning {} of {} total", dtoPage.getNumberOfElements(), dtoPage.getTotalElements());
        return PageResponseDTO.of(dtoPage);
    }

    @Override
    @Transactional(readOnly = true)
    public BlogResponseDTO getPublishedBlogBySlug(String slug) {
        log.info("API entry: getPublishedBlogBySlug slug={}", slug);

        Blog blog = blogRepository.findBySlug(slug)
                .filter(b -> b.getStatus() == BlogStatus.PUBLISHED)
                .orElseThrow(() -> {
                    log.warn("Published blog not found for slug={}", slug);
                    return new ResourceNotFoundException(ErrorCode.BLOG_NOT_FOUND, "No published blog found with slug: " + slug);
                });

        log.debug("Blog id={} has {} tags, {} comments", blog.getId(), blog.getTags().size(), blog.getComments().size());

        BlogResponseDTO response = blogMapper.toResponseDTO(blog);
        log.info("API exit: getPublishedBlogBySlug returning id={}", blog.getId());
        return response;
    }

    @Override
    @Transactional
    public void incrementViews(String slug) {
        log.info("API entry: incrementViews slug={}", slug);
        int updated = blogRepository.incrementViewsBySlug(slug);
        if (updated == 0) {
            log.warn("View-count increment failed, no blog for slug={}", slug);
            throw new ResourceNotFoundException(ErrorCode.BLOG_NOT_FOUND, "No blog found with slug: " + slug);
        }
        log.info("API exit: incrementViews incremented slug={}", slug);
    }

    @Override
    @Transactional
    public void incrementLikes(String slug) {
        log.info("API entry: incrementLikes slug={}", slug);
        int updated = blogRepository.incrementLikesBySlug(slug);
        if (updated == 0) {
            log.warn("Like-count increment failed, no blog for slug={}", slug);
            throw new ResourceNotFoundException(ErrorCode.BLOG_NOT_FOUND, "No blog found with slug: " + slug);
        }
        log.info("API exit: incrementLikes incremented slug={}", slug);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<BlogSummaryResponseDTO> getAllBlogsForAdmin(
            BlogStatus status, int page, int size, String sortBy, String sortDirection) {

        log.info("API entry: getAllBlogsForAdmin status={}", status);

        Pageable pageable = buildPageable(page, size, sortBy, sortDirection, "createdAt");
        Specification<Blog> spec = BlogSpecification.withFilters(null, null, null, status);

        Page<Blog> blogPage = blogRepository.findAll(spec, pageable);
        Page<BlogSummaryResponseDTO> dtoPage = blogPage.map(blogMapper::toSummaryDTO);

        log.info("API exit: getAllBlogsForAdmin returning {} of {} total", dtoPage.getNumberOfElements(), dtoPage.getTotalElements());
        return PageResponseDTO.of(dtoPage);
    }

    @Override
    @Transactional(readOnly = true)
    public BlogResponseDTO getBlogByIdForAdmin(Long id) {
        log.info("API entry: getBlogByIdForAdmin id={}", id);

        Blog blog = blogRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Blog not found id={}", id);
                    return new ResourceNotFoundException(ErrorCode.BLOG_NOT_FOUND, "No blog found with id: " + id);
                });

        BlogResponseDTO response = blogMapper.toResponseDTO(blog);
        log.info("API exit: getBlogByIdForAdmin returning id={}", id);
        return response;
    }

    @Override
    @Transactional
    public BlogResponseDTO createBlog(BlogRequestDTO requestDTO, Long authorUserId) {
        log.info("API entry: createBlog title={}", requestDTO.getTitle());

        String slug = SlugUtil.toSlug(requestDTO.getTitle());
        if (blogRepository.existsBySlug(slug)) {
            log.warn("Duplicate slug on create: {}", slug);
            throw new DuplicateResourceException(ErrorCode.BLOG_DUPLICATE_SLUG,
                    "A blog post resolving to slug '" + slug + "' already exists. Use a different title.");
        }

        Category category = resolveCategory(requestDTO.getCategoryId());
        User author = userRepository.findById(authorUserId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.USER_NOT_FOUND,
                        "Authenticated user id " + authorUserId + " no longer exists"));
        Set<BlogTag> tags = resolveTags(requestDTO.getTagNames());

        Blog blog = Blog.builder()
                .title(requestDTO.getTitle())
                .slug(slug)
                .contentMarkdown(requestDTO.getContentMarkdown())
                .excerpt(requestDTO.getExcerpt())
                .coverImageUrl(requestDTO.getCoverImageUrl())
                .category(category)
                .author(author)
                .status(requestDTO.getStatus())
                .readingTimeMinutes(estimateReadingTimeMinutes(requestDTO.getContentMarkdown()))
                .publishedAt(requestDTO.getStatus() == BlogStatus.PUBLISHED ? OffsetDateTime.now() : null)
                .build();

        blog.replaceTags(tags);

        Blog saved = blogRepository.save(blog);
        log.debug("Persisted blog id={} slug={}", saved.getId(), saved.getSlug());

        log.info("API exit: createBlog created id={}", saved.getId());
        return blogMapper.toResponseDTO(saved);
    }

    @Override
    @Transactional
    public BlogResponseDTO updateBlog(Long id, BlogRequestDTO requestDTO) {
        log.info("API entry: updateBlog id={}", id);

        Blog blog = blogRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Blog not found for update, id={}", id);
                    return new ResourceNotFoundException(ErrorCode.BLOG_NOT_FOUND, "No blog found with id: " + id);
                });

        String newSlug = SlugUtil.toSlug(requestDTO.getTitle());
        if (!newSlug.equals(blog.getSlug()) && blogRepository.existsBySlugAndIdNot(newSlug, id)) {
            log.warn("Duplicate slug on update: {} (blog id={})", newSlug, id);
            throw new DuplicateResourceException(ErrorCode.BLOG_DUPLICATE_SLUG,
                    "A different blog post already resolves to slug '" + newSlug + "'. Use a different title.");
        }

        Category category = resolveCategory(requestDTO.getCategoryId());
        Set<BlogTag> tags = resolveTags(requestDTO.getTagNames());

        // publishedAt is set the FIRST time a post transitions to PUBLISHED
        // and never overwritten after that — so re-saving an already
        // published post, or archiving and republishing it, does not reset
        // its original publish date. This is a deliberate editorial
        // decision, not an oversight: flag it if you want republishing to
        // bump the date instead.
        boolean transitioningToPublished = requestDTO.getStatus() == BlogStatus.PUBLISHED && blog.getPublishedAt() == null;

        blog.setTitle(requestDTO.getTitle());
        blog.setSlug(newSlug);
        blog.setContentMarkdown(requestDTO.getContentMarkdown());
        blog.setExcerpt(requestDTO.getExcerpt());
        blog.setCoverImageUrl(requestDTO.getCoverImageUrl());
        blog.setCategory(category);
        blog.setStatus(requestDTO.getStatus());
        blog.setReadingTimeMinutes(estimateReadingTimeMinutes(requestDTO.getContentMarkdown()));
        if (transitioningToPublished) {
            blog.setPublishedAt(OffsetDateTime.now());
        }
        blog.replaceTags(tags);

        Blog saved = blogRepository.save(blog);
        log.debug("Updated blog id={}", saved.getId());

        log.info("API exit: updateBlog updated id={}", saved.getId());
        return blogMapper.toResponseDTO(saved);
    }

    @Override
    @Transactional
    public void deleteBlog(Long id) {
        log.info("API entry: deleteBlog id={}", id);

        if (!blogRepository.existsById(id)) {
            log.warn("Blog not found for delete, id={}", id);
            throw new ResourceNotFoundException(ErrorCode.BLOG_NOT_FOUND, "No blog found with id: " + id);
        }

        // ON DELETE CASCADE on blog_tag_map and blog_comments handles
        // cleanup of tag associations and comments automatically.
        blogRepository.deleteById(id);
        log.info("API exit: deleteBlog deleted id={}", id);
    }

    // ------------------------------------------------------------------
    // Private helpers
    // ------------------------------------------------------------------

    private Pageable buildPageable(int page, int size, String sortBy, String sortDirection, String defaultSortBy) {
        int safePage = Math.max(page, AppConstants.DEFAULT_PAGE_NUMBER);
        int safeSize = size <= 0 ? AppConstants.DEFAULT_PAGE_SIZE : Math.min(size, AppConstants.MAX_PAGE_SIZE);
        String safeSortBy = sortBy == null || sortBy.isBlank() ? defaultSortBy : sortBy;
        Sort.Direction direction = "ASC".equalsIgnoreCase(sortDirection) ? Sort.Direction.ASC : Sort.Direction.DESC;
        return PageRequest.of(safePage, safeSize, Sort.by(direction, safeSortBy));
    }

    private Category resolveCategory(Long categoryId) {
        if (categoryId == null) {
            return null;
        }
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.CATEGORY_NOT_FOUND,
                        "No category found with id: " + categoryId));
        if (category.getType() != CategoryType.BLOG) {
            log.warn("Category id={} has type={}, expected BLOG", categoryId, category.getType());
            throw new BusinessValidationException(ErrorCode.CATEGORY_INVALID_TYPE,
                    "Category id " + categoryId + " is of type " + category.getType() + ", expected BLOG");
        }
        return category;
    }

    /**
     * Find-or-create per tag name. NOT batched into a single findAllBySlugIn
     * + bulk-insert-missing because tag creation here is rare (a handful of
     * tags per post, on a single-admin site) — the simplicity of one
     * query per tag outweighs the complexity of a batch upsert for this
     * volume. Revisit if blog authoring ever becomes multi-admin/concurrent.
     */
    private Set<BlogTag> resolveTags(List<String> tagNames) {
        if (tagNames == null || tagNames.isEmpty()) {
            return new LinkedHashSet<>();
        }
        Set<BlogTag> tags = new LinkedHashSet<>();
        for (String name : tagNames) {
            String slug = SlugUtil.toSlug(name);
            BlogTag tag = blogTagRepository.findBySlug(slug)
                    .orElseGet(() -> blogTagRepository.save(BlogTag.builder().name(name).slug(slug).build()));
            tags.add(tag);
        }
        return tags;
    }

    private int estimateReadingTimeMinutes(String contentMarkdown) {
        if (contentMarkdown == null || contentMarkdown.isBlank()) {
            return 0;
        }
        int wordCount = contentMarkdown.trim().split("\\s+").length;
        return Math.max(1, Math.round((float) wordCount / WORDS_PER_MINUTE));
    }
}
