package com.manjith.portfolio.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeoResponseDTO {
    private Long id;
    private String pagePath;
    private String metaTitle;
    private String metaDescription;
    private String ogImageUrl;
    private String canonicalUrl;
    private OffsetDateTime updatedAt;
}
