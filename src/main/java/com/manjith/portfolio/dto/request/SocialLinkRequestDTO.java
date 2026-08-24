package com.manjith.portfolio.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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
public class SocialLinkRequestDTO {

    // Deliberately not a URL pattern — url below is allowed to be
    // mailto:/tel: as well as http(s), per the seed data
    // (mailto:manjith9989@gmail.com, tel:+919398303933).
    @NotBlank(message = "platform must not be blank")
    @Size(max = 50, message = "platform must not exceed 50 characters")
    private String platform;

    @NotBlank(message = "url must not be blank")
    @Pattern(regexp = "^(https?://|mailto:|tel:).+", message = "url must start with http://, https://, mailto:, or tel:")
    @Size(max = 500, message = "url must not exceed 500 characters")
    private String url;

    @Size(max = 100, message = "icon must not exceed 100 characters")
    private String icon;

    @NotNull(message = "displayOrder must be provided")
    @Min(value = 0, message = "displayOrder must not be negative")
    private Integer displayOrder;
}
