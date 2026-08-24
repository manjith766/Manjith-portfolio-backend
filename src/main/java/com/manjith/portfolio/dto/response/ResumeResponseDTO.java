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
public class ResumeResponseDTO {
    private Long id;
    private String fileUrl;
    private String fileName;
    private Integer version;
    private Boolean isActive;
    private OffsetDateTime uploadedAt;
}
