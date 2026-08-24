package com.manjith.portfolio.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;

/**
 * Used for BOTH create (POST) and update (PUT) — the request shape is
 * identical for a full replace on update, so a single DTO avoids
 * duplicating the same 10 validated fields across two near-identical
 * classes. `id` is never part of the body; it comes from the path
 * variable on update and is server-generated on create.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectRequestDTO {

    private static final String URL_PATTERN =
            "^https?://[\\w.-]+(:\\d+)?(/[\\w\\-./?%&=]*)?$";

    @NotBlank(message = "Title must not be blank")
    @Size(max = 200, message = "Title must not exceed 200 characters")
    private String title;

    @NotBlank(message = "Description must not be blank")
    private String description;

    @Size(max = 500, message = "Short description must not exceed 500 characters")
    private String shortDescription;

    @Positive(message = "categoryId must be a positive number")
    private Long categoryId;

    @Pattern(regexp = URL_PATTERN, message = "githubUrl must be a valid http(s) URL")
    @Size(max = 500)
    private String githubUrl;

    @Pattern(regexp = URL_PATTERN, message = "liveDemoUrl must be a valid http(s) URL")
    @Size(max = 500)
    private String liveDemoUrl;

    @NotBlank(message = "coverImageUrl must not be blank")
    @Pattern(regexp = URL_PATTERN, message = "coverImageUrl must be a valid http(s) URL")
    @Size(max = 500)
    private String coverImageUrl;

    @NotNull(message = "isFeatured must be provided")
    private Boolean isFeatured;

    @NotNull(message = "displayOrder must be provided")
    @Min(value = 0, message = "displayOrder must not be negative")
    private Integer displayOrder;

    @NotNull(message = "skillIds must be provided (use an empty set if none)")
    private Set<@Positive(message = "skillIds entries must be positive") Long> skillIds;

    @NotNull(message = "features must be provided (use an empty list if none)")
    private List<@NotBlank(message = "feature text must not be blank")
                 @Size(max = 500, message = "feature text must not exceed 500 characters")
                 String> features;

    @NotNull(message = "images must be provided (use an empty list if none)")
    @Valid
    private List<ProjectImageRequestDTO> images;
}
