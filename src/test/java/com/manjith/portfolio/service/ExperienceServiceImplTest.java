package com.manjith.portfolio.service;

import com.manjith.portfolio.dto.request.ExperienceRequestDTO;
import com.manjith.portfolio.exception.BusinessValidationException;
import com.manjith.portfolio.exception.ResourceNotFoundException;
import com.manjith.portfolio.mapper.ExperienceMapper;
import com.manjith.portfolio.repository.ExperienceRepository;
import com.manjith.portfolio.repository.SkillRepository;
import com.manjith.portfolio.service.impl.ExperienceServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExperienceServiceImplTest {

    @Mock
    private ExperienceRepository experienceRepository;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private ExperienceMapper experienceMapper;

    @InjectMocks
    private ExperienceServiceImpl experienceService;

    private ExperienceRequestDTO.ExperienceRequestDTOBuilder baseBuilder;

    @BeforeEach
    void setUp() {
        baseBuilder = ExperienceRequestDTO.builder()
                .companyName("Jippy Mart")
                .role("Java Full Stack Developer")
                .location("Hyderabad")
                .startDate(LocalDate.of(2026, 4, 1))
                .displayOrder(0)
                .responsibilities(List.of("Built REST APIs"))
                .achievements(Collections.emptyList())
                .skillIds(Set.of());
    }

    @Test
    void createExperience_throwsBusinessValidationException_whenCurrentRoleHasEndDate() {
        ExperienceRequestDTO request = baseBuilder
                .isCurrent(true)
                .endDate(LocalDate.of(2026, 12, 31))
                .build();

        assertThatThrownBy(() -> experienceService.createExperience(request))
                .isInstanceOf(BusinessValidationException.class);

        verify(experienceRepository, never()).save(any());
    }

    @Test
    void createExperience_throwsBusinessValidationException_whenPastRoleMissingEndDate() {
        ExperienceRequestDTO request = baseBuilder
                .isCurrent(false)
                .endDate(null)
                .build();

        assertThatThrownBy(() -> experienceService.createExperience(request))
                .isInstanceOf(BusinessValidationException.class);

        verify(experienceRepository, never()).save(any());
    }

    @Test
    void createExperience_throwsBusinessValidationException_whenEndDateBeforeStartDate() {
        ExperienceRequestDTO request = baseBuilder
                .isCurrent(false)
                .startDate(LocalDate.of(2026, 6, 1))
                .endDate(LocalDate.of(2026, 1, 1))
                .build();

        assertThatThrownBy(() -> experienceService.createExperience(request))
                .isInstanceOf(BusinessValidationException.class);

        verify(experienceRepository, never()).save(any());
    }

    @Test
    void deleteExperience_throwsResourceNotFoundException_whenNotExists() {
        when(experienceRepository.existsById(42L)).thenReturn(false);

        assertThatThrownBy(() -> experienceService.deleteExperience(42L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(experienceRepository, never()).deleteById(any());
    }
}
