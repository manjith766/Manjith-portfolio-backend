package com.manjith.portfolio.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * No separate "summary" DTO for skills, unlike Projects — skills carry no
 * heavy child collections (images/features), so the full detail shape is
 * already light enough to use for both list and single-item views.
 * projectTitles is populated via Hibernate batch-fetching (see
 * hibernate.default_batch_fetch_size in application.yml), same strategy
 * as ProjectSummaryResponseDTO.skillNames — see SkillServiceImpl Javadoc.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SkillResponseDTO {
    private Long id;
    private String name;
    private CategorySummaryResponseDTO category;
    private Short proficiencyPct;
    private BigDecimal yearsExperience;
    private String iconUrl;
    private Integer displayOrder;
    private List<String> projectTitles;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
