package com.manjith.portfolio.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

/**
 * Deliberately omits email — commenter email is private data, only needed
 * internally for spam moderation (see AdminBlogCommentResponseDTO).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlogCommentResponseDTO {
    private Long id;
    private String name;
    private String comment;
    private OffsetDateTime createdAt;
}
