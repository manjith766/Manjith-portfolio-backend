package com.manjith.portfolio.service;

import com.manjith.portfolio.dto.request.SkillRequestDTO;
import com.manjith.portfolio.entity.Category;
import com.manjith.portfolio.entity.CategoryType;
import com.manjith.portfolio.exception.BusinessValidationException;
import com.manjith.portfolio.exception.DuplicateResourceException;
import com.manjith.portfolio.exception.ResourceNotFoundException;
import com.manjith.portfolio.mapper.SkillMapper;
import com.manjith.portfolio.repository.CategoryRepository;
import com.manjith.portfolio.repository.SkillRepository;
import com.manjith.portfolio.service.impl.SkillServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SkillServiceImplTest {

    @Mock
    private SkillRepository skillRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private SkillMapper skillMapper;

    @InjectMocks
    private SkillServiceImpl skillService;

    private SkillRequestDTO validRequest;

    @BeforeEach
    void setUp() {
        validRequest = SkillRequestDTO.builder()
                .name("Spring Boot")
                .categoryId(1L)
                .proficiencyPct((short) 90)
                .yearsExperience(new BigDecimal("2.0"))
                .iconUrl(null)
                .displayOrder(0)
                .build();
    }

    @Test
    void createSkill_throwsDuplicateResourceException_whenNameAlreadyExists() {
        when(skillRepository.existsByName("Spring Boot")).thenReturn(true);

        assertThatThrownBy(() -> skillService.createSkill(validRequest))
                .isInstanceOf(DuplicateResourceException.class);

        verify(skillRepository, never()).save(any());
    }

    @Test
    void createSkill_throwsResourceNotFoundException_whenCategoryMissing() {
        when(skillRepository.existsByName(anyString())).thenReturn(false);
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> skillService.createSkill(validRequest))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(skillRepository, never()).save(any());
    }

    @Test
    void createSkill_throwsBusinessValidationException_whenCategoryTypeIsNotSkill() {
        Category projectCategory = Category.builder().id(1L).name("Backend").slug("backend").type(CategoryType.PROJECT).build();

        when(skillRepository.existsByName(anyString())).thenReturn(false);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(projectCategory));

        assertThatThrownBy(() -> skillService.createSkill(validRequest))
                .isInstanceOf(BusinessValidationException.class);

        verify(skillRepository, never()).save(any());
    }

    @Test
    void deleteSkill_throwsResourceNotFoundException_whenSkillDoesNotExist() {
        when(skillRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> skillService.deleteSkill(99L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(skillRepository, never()).deleteById(any());
    }

    @Test
    void getSkillById_throwsResourceNotFoundException_whenNotFound() {
        when(skillRepository.findById(42L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> skillService.getSkillById(42L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
