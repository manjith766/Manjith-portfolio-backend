package com.manjith.portfolio.dto.request;

import jakarta.validation.constraints.NotBlank;
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
public class SeoRequestDTO {

    private static final String URL_PATTERN =
            "^https?://[\\w.-]+(:\\d+)?(/[\\w\\-./?%&=]*)?$";

    // e.g. "/", "/about", "/projects" — validated as a URL path, not a
    // full URL, since this identifies a frontend route, not an address.
    @NotBlank(message = "pagePath must not be blank")
    @Pattern(regexp = "^/[\\w\\-/]*$", message = "pagePath must start with '/' and contain only URL-safe path characters")
    @Size(max = 255, message = "pagePath must not exceed 255 characters")
    private String pagePath;

    @NotBlank(message = "metaTitle must not be blank")
    @Size(max = 255, message = "metaTitle must not exceed 255 characters")
    private String metaTitle;

    @Size(max = 500, message = "metaDescription must not exceed 500 characters")
    private String metaDescription;

    @Pattern(regexp = URL_PATTERN, message = "ogImageUrl must be a valid http(s) URL")
    @Size(max = 500)
    private String ogImageUrl;

    @Pattern(regexp = URL_PATTERN, message = "canonicalUrl must be a valid http(s) URL")
    @Size(max = 500)
    private String canonicalUrl;
}
