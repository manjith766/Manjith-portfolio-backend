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

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CertificationRequestDTO {

    private static final String URL_PATTERN =
            "^https?://[\\w.-]+(:\\d+)?(/[\\w\\-./?%&=]*)?$";

    @NotBlank(message = "title must not be blank")
    @Size(max = 200, message = "title must not exceed 200 characters")
    private String title;

    @NotBlank(message = "issuer must not be blank")
    @Size(max = 200, message = "issuer must not exceed 200 characters")
    private String issuer;

    @NotNull(message = "issueDate must be provided")
    private LocalDate issueDate;

    @Pattern(regexp = URL_PATTERN, message = "credentialUrl must be a valid http(s) URL")
    @Size(max = 500)
    private String credentialUrl;

    @Pattern(regexp = URL_PATTERN, message = "imageUrl must be a valid http(s) URL")
    @Size(max = 500)
    private String imageUrl;

    @NotNull(message = "displayOrder must be provided")
    @Min(value = 0, message = "displayOrder must not be negative")
    private Integer displayOrder;
}
