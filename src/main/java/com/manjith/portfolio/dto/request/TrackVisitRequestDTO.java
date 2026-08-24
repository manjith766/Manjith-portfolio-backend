package com.manjith.portfolio.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Deliberately does NOT accept ipAddress or userAgent from the client —
 * AnalyticsController extracts both from the actual HTTP request
 * (headers / remote address) so a caller can't spoof analytics data by
 * putting an arbitrary IP in a JSON body.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrackVisitRequestDTO {

    @NotBlank(message = "pagePath must not be blank")
    @Size(max = 255, message = "pagePath must not exceed 255 characters")
    private String pagePath;

    @Size(max = 500, message = "referrer must not exceed 500 characters")
    private String referrer;
}
