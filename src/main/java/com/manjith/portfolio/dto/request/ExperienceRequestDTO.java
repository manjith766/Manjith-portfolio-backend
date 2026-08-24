package com.manjith.portfolio.dto.request;

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

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExperienceRequestDTO {

    @NotBlank(message = "companyName must not be blank")
    @Size(max = 200, message = "companyName must not exceed 200 characters")
    private String companyName;

    @NotBlank(message = "role must not be blank")
    @Size(max = 200, message = "role must not exceed 200 characters")
    private String role;

    @Size(max = 200, message = "location must not exceed 200 characters")
    private String location;

    @NotNull(message = "startDate must be provided")
    private LocalDate startDate;

    // Nullable by design: null + isCurrent=true means "still employed here".
    // Cross-field validity (endDate required unless isCurrent, endDate >=
    // startDate) is enforced in ExperienceServiceImpl, not here — Jakarta
    // Bean Validation on a single DTO can't cleanly express "either/or"
    // rules between two fields without a custom class-level constraint,
    // and a custom constraint here would just reimplement what the
    // service already has to check against the DB-level CHECK constraint.
    private LocalDate endDate;

    @NotNull(message = "isCurrent must be provided")
    private Boolean isCurrent;

    private String description;

    @NotNull(message = "displayOrder must be provided")
    @Min(value = 0, message = "displayOrder must not be negative")
    private Integer displayOrder;

    @NotNull(message = "responsibilities must be provided (use an empty list if none)")
    private List<@NotBlank(message = "responsibility text must not be blank")
                 @Size(max = 500, message = "responsibility text must not exceed 500 characters")
                 String> responsibilities;

    @NotNull(message = "achievements must be provided (use an empty list if none)")
    private List<@NotBlank(message = "achievement text must not be blank")
                 @Size(max = 500, message = "achievement text must not exceed 500 characters")
                 String> achievements;

    @NotNull(message = "skillIds must be provided (use an empty set if none)")
    private Set<@Positive(message = "skillIds entries must be positive") Long> skillIds;
}
