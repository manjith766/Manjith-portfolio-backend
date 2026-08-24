package com.manjith.portfolio.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
public class BlogCommentRequestDTO {

    @NotBlank(message = "name must not be blank")
    @Size(max = 150, message = "name must not exceed 150 characters")
    private String name;

    @NotBlank(message = "email must not be blank")
    @Email(message = "email must be a valid email address")
    @Size(max = 255, message = "email must not exceed 255 characters")
    private String email;

    @NotBlank(message = "comment must not be blank")
    @Size(max = 2000, message = "comment must not exceed 2000 characters")
    private String comment;
}
