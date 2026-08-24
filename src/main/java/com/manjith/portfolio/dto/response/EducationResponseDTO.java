package com.manjith.portfolio.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EducationResponseDTO {
    private Long id;
    private String degree;
    private String institution;
    private String location;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal cgpa;
    private String description;
    private Integer displayOrder;
    private List<String> achievements;
}
