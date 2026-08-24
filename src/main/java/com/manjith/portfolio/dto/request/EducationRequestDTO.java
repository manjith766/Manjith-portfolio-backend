package com.manjith.portfolio.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class EducationRequestDTO {

    @NotBlank(message = "degree must not be blank")
    @Size(max = 200, message = "degree must not exceed 200 characters")
    private String degree;

    @NotBlank(message = "institution must not be blank")
    @Size(max = 200, message = "institution must not exceed 200 characters")
    private String institution;

    @Size(max = 200, message = "location must not exceed 200 characters")
    private String location;

    @NotNull(message = "startDate must be provided")
    private LocalDate startDate;

    // Nullable by design: null means "ongoing" (no isCurrent flag exists on
    // this table, unlike experience — a null endDate is the only signal).
    // Cross-field check (endDate >= startDate) enforced in
    // EducationServiceImpl, mirroring chk_education_dates.
    private LocalDate endDate;

    @DecimalMin(value = "0.0", message = "cgpa must not be negative")
    @DecimalMax(value = "10.0", message = "cgpa must not exceed 10.0")
    private BigDecimal cgpa;

    private String description;

    @NotNull(message = "displayOrder must be provided")
    @Min(value = 0, message = "displayOrder must not be negative")
    private Integer displayOrder;

    @NotNull(message = "achievements must be provided (use an empty list if none)")
    private List<@NotBlank(message = "achievement text must not be blank")
                 @Size(max = 500, message = "achievement text must not exceed 500 characters")
                 String> achievements;
}
