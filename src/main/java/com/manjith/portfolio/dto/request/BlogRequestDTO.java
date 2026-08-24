package com.manjith.portfolio.dto.request;

import com.manjith.portfolio.entity.BlogStatus;
import jakarta.validation.constraints.NotBlank;
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

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlogRequestDTO {

    private static final String URL_PATTERN =
            "^https?://[\\w.-]+(:\\d+)?(/[\\w\\-./?%&=]*)?$";

    @NotBlank(message = "title must not be blank")
    @Size(max = 255, message = "title must not exceed 255 characters")
    private String title;

    @NotBlank(message = "contentMarkdown must not be blank")
    private String contentMarkdown;

    @Size(max = 500, message = "excerpt must not exceed 500 characters")
    private String excerpt;

    @Pattern(regexp = URL_PATTERN, message = "coverImageUrl must be a valid http(s) URL")
    @Size(max = 500)
    private String coverImageUrl;

    @Positive(message = "categoryId must be a positive number")
    private Long categoryId;

    @NotNull(message = "status must be provided")
    private BlogStatus status;

    @NotNull(message = "tagNames must be provided (use an empty list if none)")
    private List<@NotBlank(message = "tag name must not be blank")
                 @Size(max = 100, message = "tag name must not exceed 100 characters")
                 String> tagNames;
}
