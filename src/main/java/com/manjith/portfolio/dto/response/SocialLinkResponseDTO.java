package com.manjith.portfolio.dto.response;

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
public class SocialLinkResponseDTO {
    private Long id;
    private String platform;
    private String url;
    private String icon;
    private Integer displayOrder;
}
