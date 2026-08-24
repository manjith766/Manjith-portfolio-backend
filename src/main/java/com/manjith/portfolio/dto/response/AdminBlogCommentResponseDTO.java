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
public class AdminBlogCommentResponseDTO {
    private Long id;
    private Long blogId;
    private String blogTitle;
    private String name;
    private String email;
    private String comment;
    private Boolean isApproved;
    private OffsetDateTime createdAt;
}
