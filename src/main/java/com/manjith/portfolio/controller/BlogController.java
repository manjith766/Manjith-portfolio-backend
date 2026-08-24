package com.manjith.portfolio.controller;

import com.manjith.portfolio.constants.AppConstants;
import com.manjith.portfolio.dto.response.BlogResponseDTO;
import com.manjith.portfolio.dto.response.BlogSummaryResponseDTO;
import com.manjith.portfolio.dto.response.PageResponseDTO;
import com.manjith.portfolio.service.BlogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Public, unauthenticated endpoints. Every method delegates straight to
 * BlogService, which itself hardcodes PUBLISHED-only visibility — this
 * controller never passes a status parameter, so there is no way for a
 * caller-supplied query param to leak drafts.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "Blog", description = "Public blog listing and detail endpoints")
public class BlogController {

    private final BlogService blogService;

    @GetMapping("/api/blog")
    @Operation(summary = "List published blog posts with pagination, filtering, and search")
    public ResponseEntity<PageResponseDTO<BlogSummaryResponseDTO>> getPublishedBlogs(
            @RequestParam(required = false) String categorySlug,
            @RequestParam(required = false) String tagSlug,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "" + AppConstants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(defaultValue = "" + AppConstants.DEFAULT_PAGE_SIZE) int size,
            @RequestParam(defaultValue = "publishedAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection) {

        log.info("GET /api/blog categorySlug={}, tagSlug={}, search={}, page={}, size={}",
                categorySlug, tagSlug, search, page, size);
        PageResponseDTO<BlogSummaryResponseDTO> result =
                blogService.getPublishedBlogs(categorySlug, tagSlug, search, page, size, sortBy, sortDirection);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/api/blog/{slug}")
    @Operation(summary = "Get a single published blog post by slug")
    public ResponseEntity<BlogResponseDTO> getPublishedBlogBySlug(@PathVariable String slug) {
        log.info("GET /api/blog/{}", slug);
        BlogResponseDTO result = blogService.getPublishedBlogBySlug(slug);
        return ResponseEntity.ok(result);
    }

    @PatchMapping("/api/blog/{slug}/view")
    @Operation(summary = "Atomically increment the view count for a blog post")
    public ResponseEntity<Void> incrementViews(@PathVariable String slug) {
        log.info("PATCH /api/blog/{}/view", slug);
        blogService.incrementViews(slug);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/api/blog/{slug}/like")
    @Operation(summary = "Atomically increment the like count for a blog post")
    public ResponseEntity<Void> incrementLikes(@PathVariable String slug) {
        log.info("PATCH /api/blog/{}/like", slug);
        blogService.incrementLikes(slug);
        return ResponseEntity.noContent().build();
    }
}
