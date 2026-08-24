package com.manjith.portfolio.dto.response;

import com.manjith.portfolio.entity.BlogStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Excludes contentMarkdown deliberately — a list of dozens of blog posts
 * should not each carry their full body over the wire. Same tradeoff as
 * ProjectSummaryResponseDTO vs ProjectResponseDTO.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlogSummaryResponseDTO {
    private Long id;
    private String title;
    private String slug;
    private String excerpt;
    private String coverImageUrl;
    private String categoryName;
    private BlogStatus status;
    private Long views;
    private Long likes;
    private Integer readingTimeMinutes;
    private OffsetDateTime publishedAt;
    private List<String> tags;
}
