package com.manjith.portfolio.mapper;

import com.manjith.portfolio.dto.response.AuthorSummaryResponseDTO;
import com.manjith.portfolio.dto.response.BlogResponseDTO;
import com.manjith.portfolio.dto.response.BlogSummaryResponseDTO;
import com.manjith.portfolio.entity.Blog;
import com.manjith.portfolio.entity.Category;
import com.manjith.portfolio.entity.User;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-08-22T17:19:48+0530",
    comments = "version: 1.6.2, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
)
@Component
public class BlogMapperImpl implements BlogMapper {

    @Autowired
    private CommonMapper commonMapper;

    @Override
    public BlogResponseDTO toResponseDTO(Blog blog) {
        if ( blog == null ) {
            return null;
        }

        BlogResponseDTO.BlogResponseDTOBuilder blogResponseDTO = BlogResponseDTO.builder();

        blogResponseDTO.category( commonMapper.toCategorySummaryDTO( blog.getCategory() ) );
        blogResponseDTO.author( toAuthorSummaryDTO( blog.getAuthor() ) );
        blogResponseDTO.id( blog.getId() );
        blogResponseDTO.title( blog.getTitle() );
        blogResponseDTO.slug( blog.getSlug() );
        blogResponseDTO.contentMarkdown( blog.getContentMarkdown() );
        blogResponseDTO.excerpt( blog.getExcerpt() );
        blogResponseDTO.coverImageUrl( blog.getCoverImageUrl() );
        blogResponseDTO.status( blog.getStatus() );
        blogResponseDTO.views( blog.getViews() );
        blogResponseDTO.likes( blog.getLikes() );
        blogResponseDTO.readingTimeMinutes( blog.getReadingTimeMinutes() );
        blogResponseDTO.publishedAt( blog.getPublishedAt() );
        blogResponseDTO.createdAt( blog.getCreatedAt() );
        blogResponseDTO.updatedAt( blog.getUpdatedAt() );

        blogResponseDTO.tags( mapTagNames(blog.getTags()) );

        return blogResponseDTO.build();
    }

    @Override
    public BlogSummaryResponseDTO toSummaryDTO(Blog blog) {
        if ( blog == null ) {
            return null;
        }

        BlogSummaryResponseDTO.BlogSummaryResponseDTOBuilder blogSummaryResponseDTO = BlogSummaryResponseDTO.builder();

        blogSummaryResponseDTO.categoryName( blogCategoryName( blog ) );
        blogSummaryResponseDTO.id( blog.getId() );
        blogSummaryResponseDTO.title( blog.getTitle() );
        blogSummaryResponseDTO.slug( blog.getSlug() );
        blogSummaryResponseDTO.excerpt( blog.getExcerpt() );
        blogSummaryResponseDTO.coverImageUrl( blog.getCoverImageUrl() );
        blogSummaryResponseDTO.status( blog.getStatus() );
        blogSummaryResponseDTO.views( blog.getViews() );
        blogSummaryResponseDTO.likes( blog.getLikes() );
        blogSummaryResponseDTO.readingTimeMinutes( blog.getReadingTimeMinutes() );
        blogSummaryResponseDTO.publishedAt( blog.getPublishedAt() );

        blogSummaryResponseDTO.tags( mapTagNames(blog.getTags()) );

        return blogSummaryResponseDTO.build();
    }

    @Override
    public AuthorSummaryResponseDTO toAuthorSummaryDTO(User user) {
        if ( user == null ) {
            return null;
        }

        AuthorSummaryResponseDTO.AuthorSummaryResponseDTOBuilder authorSummaryResponseDTO = AuthorSummaryResponseDTO.builder();

        authorSummaryResponseDTO.username( user.getUsername() );

        return authorSummaryResponseDTO.build();
    }

    private String blogCategoryName(Blog blog) {
        Category category = blog.getCategory();
        if ( category == null ) {
            return null;
        }
        return category.getName();
    }
}
