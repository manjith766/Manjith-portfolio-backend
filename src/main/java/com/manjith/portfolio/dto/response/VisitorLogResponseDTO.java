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
public class VisitorLogResponseDTO {
    private Long id;
    private String ipAddress;
    private String userAgent;
    private String pagePath;
    private String referrer;
    private String country;
    private String city;
    private OffsetDateTime visitedAt;
}
