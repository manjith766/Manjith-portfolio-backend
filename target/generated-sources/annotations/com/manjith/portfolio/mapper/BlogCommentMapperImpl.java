package com.manjith.portfolio.mapper;

import com.manjith.portfolio.dto.response.AdminBlogCommentResponseDTO;
import com.manjith.portfolio.dto.response.BlogCommentResponseDTO;
import com.manjith.portfolio.entity.Blog;
import com.manjith.portfolio.entity.BlogComment;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-08-22T17:19:48+0530",
    comments = "version: 1.6.2, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
)
@Component
public class BlogCommentMapperImpl implements BlogCommentMapper {

    @Override
    public BlogCommentResponseDTO toResponseDTO(BlogComment comment) {
        if ( comment == null ) {
            return null;
        }

        BlogCommentResponseDTO.BlogCommentResponseDTOBuilder blogCommentResponseDTO = BlogCommentResponseDTO.builder();

        blogCommentResponseDTO.id( comment.getId() );
        blogCommentResponseDTO.name( comment.getName() );
        blogCommentResponseDTO.comment( comment.getComment() );
        blogCommentResponseDTO.createdAt( comment.getCreatedAt() );

        return blogCommentResponseDTO.build();
    }

    @Override
    public AdminBlogCommentResponseDTO toAdminResponseDTO(BlogComment comment) {
        if ( comment == null ) {
            return null;
        }

        AdminBlogCommentResponseDTO.AdminBlogCommentResponseDTOBuilder adminBlogCommentResponseDTO = AdminBlogCommentResponseDTO.builder();

        adminBlogCommentResponseDTO.blogId( commentBlogId( comment ) );
        adminBlogCommentResponseDTO.blogTitle( commentBlogTitle( comment ) );
        adminBlogCommentResponseDTO.id( comment.getId() );
        adminBlogCommentResponseDTO.name( comment.getName() );
        adminBlogCommentResponseDTO.email( comment.getEmail() );
        adminBlogCommentResponseDTO.comment( comment.getComment() );
        adminBlogCommentResponseDTO.isApproved( comment.getIsApproved() );
        adminBlogCommentResponseDTO.createdAt( comment.getCreatedAt() );

        return adminBlogCommentResponseDTO.build();
    }

    private Long commentBlogId(BlogComment blogComment) {
        Blog blog = blogComment.getBlog();
        if ( blog == null ) {
            return null;
        }
        return blog.getId();
    }

    private String commentBlogTitle(BlogComment blogComment) {
        Blog blog = blogComment.getBlog();
        if ( blog == null ) {
            return null;
        }
        return blog.getTitle();
    }
}
