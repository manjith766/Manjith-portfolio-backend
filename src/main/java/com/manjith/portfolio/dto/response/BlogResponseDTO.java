package com.manjith.portfolio.dto.response;

import com.manjith.portfolio.entity.BlogStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlogResponseDTO {
    private Long id;
    private String title;
    private String slug;
    private String contentMarkdown;
    private String excerpt;
    private String coverImageUrl;
    private CategorySummaryResponseDTO category;
    private AuthorSummaryResponseDTO author;
    private BlogStatus status;
    private Long views;
    private Long likes;
    private Integer readingTimeMinutes;
    private OffsetDateTime publishedAt;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private List<String> tags;
}
