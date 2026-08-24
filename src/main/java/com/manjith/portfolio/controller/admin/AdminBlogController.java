package com.manjith.portfolio.controller.admin;

import com.manjith.portfolio.constants.AppConstants;
import com.manjith.portfolio.dto.request.BlogRequestDTO;
import com.manjith.portfolio.dto.response.BlogResponseDTO;
import com.manjith.portfolio.dto.response.BlogSummaryResponseDTO;
import com.manjith.portfolio.dto.response.PageResponseDTO;
import com.manjith.portfolio.entity.BlogStatus;
import com.manjith.portfolio.security.UserPrincipal;
import com.manjith.portfolio.service.BlogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

/**
 * Admin-only. Contains NO business logic — every method is a direct
 * pass-through to BlogService. The one piece of "wiring" here (not
 * business logic) is extracting the authenticated user's id from the
 * security context via @AuthenticationPrincipal so BlogService can stamp
 * the post's author — the controller does not decide WHO can author a
 * post, only WHICH already-authenticated id to forward.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin - Blog", description = "Admin-only blog management endpoints")
public class AdminBlogController {

    private final BlogService blogService;

    @GetMapping("/api/admin/blog")
    @Operation(summary = "List all blog posts regardless of status, with pagination")
    public ResponseEntity<PageResponseDTO<BlogSummaryResponseDTO>> getAllBlogsForAdmin(
            @RequestParam(required = false) BlogStatus status,
            @RequestParam(defaultValue = "" + AppConstants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(defaultValue = "" + AppConstants.DEFAULT_PAGE_SIZE) int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection) {

        log.info("GET /api/admin/blog status={}, page={}, size={}", status, page, size);
        PageResponseDTO<BlogSummaryResponseDTO> result =
                blogService.getAllBlogsForAdmin(status, page, size, sortBy, sortDirection);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/api/admin/blog/{id}")
    @Operation(summary = "Get any blog post (including drafts) by id")
    public ResponseEntity<BlogResponseDTO> getBlogByIdForAdmin(@PathVariable Long id) {
        log.info("GET /api/admin/blog/{}", id);
        BlogResponseDTO result = blogService.getBlogByIdForAdmin(id);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/api/admin/blog")
    @Operation(summary = "Create a new blog post (draft or published)")
    public ResponseEntity<BlogResponseDTO> createBlog(
            @Valid @RequestBody BlogRequestDTO requestDTO,
            @AuthenticationPrincipal UserPrincipal principal,
            UriComponentsBuilder uriBuilder) {
        log.info("POST /api/admin/blog title={}, authorId={}", requestDTO.getTitle(), principal.getId());
        BlogResponseDTO created = blogService.createBlog(requestDTO, principal.getId());
        URI location = uriBuilder.path("/api/blog/{slug}").buildAndExpand(created.getSlug()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/api/admin/blog/{id}")
    @Operation(summary = "Full update of an existing blog post")
    public ResponseEntity<BlogResponseDTO> updateBlog(
            @PathVariable Long id, @Valid @RequestBody BlogRequestDTO requestDTO) {
        log.info("PUT /api/admin/blog/{}", id);
        BlogResponseDTO updated = blogService.updateBlog(id, requestDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/api/admin/blog/{id}")
    @Operation(summary = "Delete a blog post")
    public ResponseEntity<Void> deleteBlog(@PathVariable Long id) {
        log.info("DELETE /api/admin/blog/{}", id);
        blogService.deleteBlog(id);
        return ResponseEntity.noContent().build();
    }
}
