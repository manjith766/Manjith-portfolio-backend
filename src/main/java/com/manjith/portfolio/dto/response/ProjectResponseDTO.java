package com.manjith.portfolio.dto.response;

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
public class ProjectResponseDTO {
    private Long id;
    private String title;
    private String slug;
    private String description;
    private String shortDescription;
    private CategorySummaryResponseDTO category;
    private String githubUrl;
    private String liveDemoUrl;
    private String coverImageUrl;
    private Boolean isFeatured;
    private Integer displayOrder;
    private Long viewCount;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private List<ProjectImageResponseDTO> images;
    private List<String> features;
    private List<SkillSummaryResponseDTO> skills;
}
