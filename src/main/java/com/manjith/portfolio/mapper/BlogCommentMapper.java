package com.manjith.portfolio.mapper;

import com.manjith.portfolio.dto.response.AdminBlogCommentResponseDTO;
import com.manjith.portfolio.dto.response.BlogCommentResponseDTO;
import com.manjith.portfolio.entity.BlogComment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BlogCommentMapper {

    BlogCommentResponseDTO toResponseDTO(BlogComment comment);

    @Mapping(target = "blogId", source = "blog.id")
    @Mapping(target = "blogTitle", source = "blog.title")
    AdminBlogCommentResponseDTO toAdminResponseDTO(BlogComment comment);
}
