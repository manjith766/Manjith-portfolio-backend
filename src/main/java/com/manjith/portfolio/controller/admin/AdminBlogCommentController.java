package com.manjith.portfolio.controller.admin;

import com.manjith.portfolio.constants.AppConstants;
import com.manjith.portfolio.dto.response.AdminBlogCommentResponseDTO;
import com.manjith.portfolio.dto.response.PageResponseDTO;
import com.manjith.portfolio.service.BlogCommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin - Blog Comments", description = "Comment moderation queue")
public class AdminBlogCommentController {

    private final BlogCommentService blogCommentService;

    @GetMapping("/api/admin/blog/comments/pending")
    @Operation(summary = "List comments awaiting moderation")
    public ResponseEntity<PageResponseDTO<AdminBlogCommentResponseDTO>> getPendingComments(
            @RequestParam(defaultValue = "" + AppConstants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(defaultValue = "" + AppConstants.DEFAULT_PAGE_SIZE) int size) {
        log.info("GET /api/admin/blog/comments/pending page={}, size={}", page, size);
        PageResponseDTO<AdminBlogCommentResponseDTO> result = blogCommentService.getPendingComments(page, size);
        return ResponseEntity.ok(result);
    }

    @PatchMapping("/api/admin/blog/comments/{id}/approve")
    @Operation(summary = "Approve a pending comment, making it publicly visible")
    public ResponseEntity<Void> approveComment(@PathVariable Long id) {
        log.info("PATCH /api/admin/blog/comments/{}/approve", id);
        blogCommentService.approveComment(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/api/admin/blog/comments/{id}")
    @Operation(summary = "Delete a comment (spam or rejected)")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        log.info("DELETE /api/admin/blog/comments/{}", id);
        blogCommentService.deleteComment(id);
        return ResponseEntity.noContent().build();
    }
}
