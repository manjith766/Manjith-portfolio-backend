package com.manjith.portfolio.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Deliberately excludes images/features (only used on the detail page) to
 * keep list-endpoint payloads small. skillNames is populated via Hibernate
 * batch-fetching (see application.yml: hibernate.default_batch_fetch_size)
 * so displaying it here does NOT introduce N+1 — see ProjectServiceImpl
 * class-level Javadoc for the query-count breakdown.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectSummaryResponseDTO {
    private Long id;
    private String title;
    private String slug;
    private String shortDescription;
    private String coverImageUrl;
    private String categoryName;
    private Boolean isFeatured;
    private Long viewCount;
    private List<String> skillNames;
}
