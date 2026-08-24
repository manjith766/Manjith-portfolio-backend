package com.manjith.portfolio.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExperienceResponseDTO {
    private Long id;
    private String companyName;
    private String role;
    private String location;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isCurrent;
    private String description;
    private Integer displayOrder;
    private List<String> responsibilities;
    private List<String> achievements;
    private List<SkillSummaryResponseDTO> skills;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
