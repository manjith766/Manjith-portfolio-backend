package com.manjith.portfolio.mapper;

import com.manjith.portfolio.dto.response.AuthorSummaryResponseDTO;
import com.manjith.portfolio.dto.response.BlogResponseDTO;
import com.manjith.portfolio.dto.response.BlogSummaryResponseDTO;
import com.manjith.portfolio.entity.Blog;
import com.manjith.portfolio.entity.BlogTag;
import com.manjith.portfolio.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = CommonMapper.class)
public interface BlogMapper {

    @Mapping(target = "category", source = "category")
    @Mapping(target = "author", source = "author")
    @Mapping(target = "tags", expression = "java(mapTagNames(blog.getTags()))")
    BlogResponseDTO toResponseDTO(Blog blog);

    @Mapping(target = "categoryName", source = "category.name")
    @Mapping(target = "tags", expression = "java(mapTagNames(blog.getTags()))")
    BlogSummaryResponseDTO toSummaryDTO(Blog blog);

    AuthorSummaryResponseDTO toAuthorSummaryDTO(User user);

    default List<String> mapTagNames(Set<BlogTag> tags) {
        if (tags == null) {
            return Collections.emptyList();
        }
        return tags.stream().map(BlogTag::getName).collect(Collectors.toList());
    }
}
