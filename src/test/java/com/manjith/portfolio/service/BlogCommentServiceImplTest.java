package com.manjith.portfolio.service;

import com.manjith.portfolio.dto.request.BlogCommentRequestDTO;
import com.manjith.portfolio.entity.Blog;
import com.manjith.portfolio.entity.BlogStatus;
import com.manjith.portfolio.exception.ResourceNotFoundException;
import com.manjith.portfolio.mapper.BlogCommentMapper;
import com.manjith.portfolio.repository.BlogCommentRepository;
import com.manjith.portfolio.repository.BlogRepository;
import com.manjith.portfolio.service.impl.BlogCommentServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BlogCommentServiceImplTest {

    @Mock
    private BlogCommentRepository blogCommentRepository;
    @Mock
    private BlogRepository blogRepository;
    @Mock
    private BlogCommentMapper blogCommentMapper;

    @InjectMocks
    private BlogCommentServiceImpl blogCommentService;

    @Test
    void submitComment_throwsResourceNotFoundException_whenBlogMissing() {
        when(blogRepository.findBySlug("missing")).thenReturn(Optional.empty());

        BlogCommentRequestDTO request = BlogCommentRequestDTO.builder()
                .name("Reader").email("reader@test.com").comment("Great post!").build();

        assertThatThrownBy(() -> blogCommentService.submitComment("missing", request))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(blogCommentRepository, never()).save(any());
    }

    @Test
    void submitComment_throwsResourceNotFoundException_whenBlogIsDraft() {
        Blog draft = Blog.builder().id(1L).slug("draft-post").status(BlogStatus.DRAFT).build();
        when(blogRepository.findBySlug("draft-post")).thenReturn(Optional.of(draft));

        BlogCommentRequestDTO request = BlogCommentRequestDTO.builder()
                .name("Reader").email("reader@test.com").comment("Great post!").build();

        // Commenting on a draft must fail the same way as a nonexistent
        // slug — no distinguishable error that would leak the draft's existence.
        assertThatThrownBy(() -> blogCommentService.submitComment("draft-post", request))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(blogCommentRepository, never()).save(any());
    }

    @Test
    void approveComment_throwsResourceNotFoundException_whenNotFound() {
        when(blogCommentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> blogCommentService.approveComment(1L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deleteComment_throwsResourceNotFoundException_whenNotFound() {
        when(blogCommentRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> blogCommentService.deleteComment(1L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(blogCommentRepository, never()).deleteById(any());
    }
}
