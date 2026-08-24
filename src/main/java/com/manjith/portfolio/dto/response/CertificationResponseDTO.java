package com.manjith.portfolio.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CertificationResponseDTO {
    private Long id;
    private String title;
    private String issuer;
    private LocalDate issueDate;
    private String credentialUrl;
    private String imageUrl;
    private Integer displayOrder;
}
