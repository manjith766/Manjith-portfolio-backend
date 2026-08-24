package com.manjith.portfolio.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SkillRequestDTO {

    @NotBlank(message = "name must not be blank")
    @Size(max = 100, message = "name must not exceed 100 characters")
    private String name;

    @NotNull(message = "categoryId must be provided")
    @Positive(message = "categoryId must be a positive number")
    private Long categoryId;

    @NotNull(message = "proficiencyPct must be provided")
    @Min(value = 0, message = "proficiencyPct must be between 0 and 100")
    @Max(value = 100, message = "proficiencyPct must be between 0 and 100")
    private Short proficiencyPct;

    @NotNull(message = "yearsExperience must be provided")
    @DecimalMin(value = "0.0", message = "yearsExperience must not be negative")
    private BigDecimal yearsExperience;

    @Size(max = 500, message = "iconUrl must not exceed 500 characters")
    private String iconUrl;

    @NotNull(message = "displayOrder must be provided")
    @Min(value = 0, message = "displayOrder must not be negative")
    private Integer displayOrder;
}
