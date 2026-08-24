package com.manjith.portfolio.controller;

import com.manjith.portfolio.dto.request.BlogCommentRequestDTO;
import com.manjith.portfolio.dto.response.BlogCommentResponseDTO;
import com.manjith.portfolio.service.BlogCommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "Blog Comments", description = "Public comment reading and submission")
public class BlogCommentController {

    private final BlogCommentService blogCommentService;

    @GetMapping("/api/blog/{slug}/comments")
    @Operation(summary = "List approved comments for a published blog post")
    public ResponseEntity<List<BlogCommentResponseDTO>> getApprovedComments(@PathVariable String slug) {
        log.info("GET /api/blog/{}/comments", slug);
        List<BlogCommentResponseDTO> result = blogCommentService.getApprovedComments(slug);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/api/blog/{slug}/comments")
    @Operation(summary = "Submit a comment for moderation (not immediately visible)")
    public ResponseEntity<BlogCommentResponseDTO> submitComment(
            @PathVariable String slug, @Valid @RequestBody BlogCommentRequestDTO requestDTO) {
        log.info("POST /api/blog/{}/comments", slug);
        BlogCommentResponseDTO result = blogCommentService.submitComment(slug, requestDTO);
        return ResponseEntity.status(201).body(result);
    }
}
