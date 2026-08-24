package com.manjith.portfolio.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Deliberately distinct from ProjectSummaryResponseDTO — this is used
 * inside SkillResponseDTO.projects, where only enough detail to render a
 * link/chip is needed, not the full card payload.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectMiniResponseDTO {
    private Long id;
    private String title;
    private String slug;
}
