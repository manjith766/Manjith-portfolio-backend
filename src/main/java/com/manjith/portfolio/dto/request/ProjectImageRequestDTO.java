package com.manjith.portfolio.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectImageRequestDTO {

    @NotBlank(message = "Image URL must not be blank")
    @Size(max = 500, message = "Image URL must not exceed 500 characters")
    private String imageUrl;

    @Size(max = 255, message = "Alt text must not exceed 255 characters")
    private String altText;

    @Min(value = 0, message = "Display order must not be negative")
    @Builder.Default
    private Integer displayOrder = 0;
}
