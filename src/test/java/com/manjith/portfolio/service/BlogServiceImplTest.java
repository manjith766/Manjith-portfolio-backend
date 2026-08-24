package com.manjith.portfolio.service;

import com.manjith.portfolio.dto.request.BlogRequestDTO;
import com.manjith.portfolio.entity.BlogStatus;
import com.manjith.portfolio.entity.Category;
import com.manjith.portfolio.entity.CategoryType;
import com.manjith.portfolio.exception.BusinessValidationException;
import com.manjith.portfolio.exception.DuplicateResourceException;
import com.manjith.portfolio.exception.ResourceNotFoundException;
import com.manjith.portfolio.mapper.BlogMapper;
import com.manjith.portfolio.repository.BlogRepository;
import com.manjith.portfolio.repository.BlogTagRepository;
import com.manjith.portfolio.repository.CategoryRepository;
import com.manjith.portfolio.repository.UserRepository;
import com.manjith.portfolio.service.impl.BlogServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BlogServiceImplTest {

    @Mock
    private BlogRepository blogRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private BlogTagRepository blogTagRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BlogMapper blogMapper;

    @InjectMocks
    private BlogServiceImpl blogService;

    private BlogRequestDTO.BlogRequestDTOBuilder baseBuilder;

    @BeforeEach
    void setUp() {
        baseBuilder = BlogRequestDTO.builder()
                .title("Designing a Multi-Vendor Coupon Engine")
                .contentMarkdown("## Intro\nThis post covers coupon edge cases.")
                .excerpt("A look at real-world coupon engine edge cases")
                .status(BlogStatus.DRAFT)
                .tagNames(Collections.emptyList());
    }

    @Test
    void createBlog_throwsDuplicateResourceException_whenSlugAlreadyExists() {
        when(blogRepository.existsBySlug(anyString())).thenReturn(true);

        assertThatThrownBy(() -> blogService.createBlog(baseBuilder.build(), 1L))
                .isInstanceOf(DuplicateResourceException.class);

        verify(blogRepository, never()).save(any());
    }

    @Test
    void createBlog_throwsResourceNotFoundException_whenAuthorMissing() {
        when(blogRepository.existsBySlug(anyString())).thenReturn(false);
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> blogService.createBlog(baseBuilder.build(), 1L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(blogRepository, never()).save(any());
    }

    @Test
    void createBlog_throwsBusinessValidationException_whenCategoryTypeIsNotBlog() {
        Category projectCategory = Category.builder().id(1L).name("Backend").slug("backend").type(CategoryType.PROJECT).build();

        BlogRequestDTO request = baseBuilder.categoryId(1L).build();

        when(blogRepository.existsBySlug(anyString())).thenReturn(false);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(projectCategory));

        assertThatThrownBy(() -> blogService.createBlog(request, 1L))
                .isInstanceOf(BusinessValidationException.class);

        verify(blogRepository, never()).save(any());
    }

    @Test
    void getPublishedBlogBySlug_throwsResourceNotFoundException_whenNotFound() {
        when(blogRepository.findBySlug("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> blogService.getPublishedBlogBySlug("missing"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getPublishedBlogBySlug_throwsResourceNotFoundException_whenPostIsDraft() {
        com.manjith.portfolio.entity.Blog draft = com.manjith.portfolio.entity.Blog.builder()
                .id(1L).slug("draft-post").status(BlogStatus.DRAFT).build();

        when(blogRepository.findBySlug("draft-post")).thenReturn(Optional.of(draft));

        // A draft must be invisible to the public endpoint even though the row exists.
        assertThatThrownBy(() -> blogService.getPublishedBlogBySlug("draft-post"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void incrementViews_throwsResourceNotFoundException_whenNoRowsUpdated() {
        when(blogRepository.incrementViewsBySlug("missing")).thenReturn(0);

        assertThatThrownBy(() -> blogService.incrementViews("missing"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deleteBlog_throwsResourceNotFoundException_whenNotExists() {
        when(blogRepository.existsById(42L)).thenReturn(false);

        assertThatThrownBy(() -> blogService.deleteBlog(42L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(blogRepository, never()).deleteById(any());
    }
}
