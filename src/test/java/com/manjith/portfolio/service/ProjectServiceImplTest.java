package com.manjith.portfolio.service;

import com.manjith.portfolio.dto.request.ProjectRequestDTO;
import com.manjith.portfolio.entity.Category;
import com.manjith.portfolio.entity.CategoryType;
import com.manjith.portfolio.entity.Project;
import com.manjith.portfolio.entity.Skill;
import com.manjith.portfolio.exception.BusinessValidationException;
import com.manjith.portfolio.exception.DuplicateResourceException;
import com.manjith.portfolio.exception.ResourceNotFoundException;
import com.manjith.portfolio.mapper.ProjectMapper;
import com.manjith.portfolio.repository.CategoryRepository;
import com.manjith.portfolio.repository.ProjectRepository;
import com.manjith.portfolio.repository.SkillRepository;
import com.manjith.portfolio.service.impl.ProjectServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectServiceImplTest {

    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private ProjectMapper projectMapper;

    @InjectMocks
    private ProjectServiceImpl projectService;

    private ProjectRequestDTO validRequest;

    @BeforeEach
    void setUp() {
        validRequest = ProjectRequestDTO.builder()
                .title("Multi-Vendor E-Commerce Platform")
                .description("A full backend platform with RBAC and a coupon engine.")
                .shortDescription("E-commerce backend with RBAC")
                .categoryId(1L)
                .githubUrl("https://github.com/manjith766/ecommerce")
                .liveDemoUrl(null)
                .coverImageUrl("https://res.cloudinary.com/demo/cover.png")
                .isFeatured(true)
                .displayOrder(0)
                .skillIds(Set.of(1L, 2L))
                .features(List.of("RBAC", "Coupon engine"))
                .images(Collections.emptyList())
                .build();
    }

    @Test
    void createProject_throwsDuplicateResourceException_whenSlugAlreadyExists() {
        when(projectRepository.existsBySlug(anyString())).thenReturn(true);

        assertThatThrownBy(() -> projectService.createProject(validRequest))
                .isInstanceOf(DuplicateResourceException.class);

        verify(projectRepository, never()).save(any());
    }

    @Test
    void createProject_throwsResourceNotFoundException_whenCategoryMissing() {
        when(projectRepository.existsBySlug(anyString())).thenReturn(false);
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectService.createProject(validRequest))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(projectRepository, never()).save(any());
    }

    @Test
    void createProject_throwsBusinessValidationException_whenCategoryTypeIsNotProject() {
        Category blogCategory = Category.builder().id(1L).name("Java").slug("java").type(CategoryType.SKILL).build();

        when(projectRepository.existsBySlug(anyString())).thenReturn(false);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(blogCategory));

        assertThatThrownBy(() -> projectService.createProject(validRequest))
                .isInstanceOf(BusinessValidationException.class);

        verify(projectRepository, never()).save(any());
    }

    @Test
    void createProject_throwsResourceNotFoundException_whenSkillIdsMissing() {
        Category category = Category.builder().id(1L).name("Backend").slug("backend").type(CategoryType.PROJECT).build();

        when(projectRepository.existsBySlug(anyString())).thenReturn(false);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        // Only one of the two requested skill IDs exists
        when(skillRepository.findAllByIdIn(Set.of(1L, 2L)))
                .thenReturn(List.of(Skill.builder().id(1L).name("Java").build()));

        assertThatThrownBy(() -> projectService.createProject(validRequest))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(projectRepository, never()).save(any());
    }

    @Test
    void deleteProject_throwsResourceNotFoundException_whenProjectDoesNotExist() {
        when(projectRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> projectService.deleteProject(99L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(projectRepository, never()).deleteById(any());
    }

    @Test
    void incrementViewCount_throwsResourceNotFoundException_whenNoRowsUpdated() {
        when(projectRepository.incrementViewCountBySlug("nonexistent-slug")).thenReturn(0);

        assertThatThrownBy(() -> projectService.incrementViewCount("nonexistent-slug"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void incrementViewCount_succeeds_whenRowUpdated() {
        when(projectRepository.incrementViewCountBySlug("valid-slug")).thenReturn(1);

        projectService.incrementViewCount("valid-slug");

        verify(projectRepository).incrementViewCountBySlug("valid-slug");
    }

    @Test
    void getProjectBySlug_throwsResourceNotFoundException_whenNotFound() {
        when(projectRepository.findBySlug("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectService.getProjectBySlug("missing"))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
