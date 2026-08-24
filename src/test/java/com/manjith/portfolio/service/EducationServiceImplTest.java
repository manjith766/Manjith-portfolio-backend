package com.manjith.portfolio.service;

import com.manjith.portfolio.dto.request.EducationRequestDTO;
import com.manjith.portfolio.exception.BusinessValidationException;
import com.manjith.portfolio.exception.ResourceNotFoundException;
import com.manjith.portfolio.mapper.EducationMapper;
import com.manjith.portfolio.repository.EducationRepository;
import com.manjith.portfolio.service.impl.EducationServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EducationServiceImplTest {

    @Mock
    private EducationRepository educationRepository;
    @Mock
    private EducationMapper educationMapper;

    @InjectMocks
    private EducationServiceImpl educationService;

    @Test
    void createEducation_throwsBusinessValidationException_whenEndDateBeforeStartDate() {
        EducationRequestDTO request = EducationRequestDTO.builder()
                .degree("Master of Computer Applications")
                .institution("Prakasam Engineering College")
                .startDate(LocalDate.of(2025, 5, 1))
                .endDate(LocalDate.of(2023, 5, 1))
                .cgpa(new BigDecimal("8.5"))
                .displayOrder(0)
                .achievements(Collections.emptyList())
                .build();

        assertThatThrownBy(() -> educationService.createEducation(request))
                .isInstanceOf(BusinessValidationException.class);

        verify(educationRepository, never()).save(any());
    }

    @Test
    void createEducation_succeeds_whenEndDateIsNullMeaningOngoing() {
        EducationRequestDTO request = EducationRequestDTO.builder()
                .degree("Master of Computer Applications")
                .institution("Prakasam Engineering College")
                .startDate(LocalDate.of(2023, 5, 1))
                .endDate(null)
                .cgpa(new BigDecimal("8.5"))
                .displayOrder(0)
                .achievements(Collections.emptyList())
                .build();

        when(educationRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        educationService.createEducation(request);

        verify(educationRepository).save(any());
    }

    @Test
    void deleteEducation_throwsResourceNotFoundException_whenNotExists() {
        when(educationRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> educationService.deleteEducation(99L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(educationRepository, never()).deleteById(any());
    }

    @Test
    void getEducationById_throwsResourceNotFoundException_whenNotFound() {
        when(educationRepository.findById(1L)).thenReturn(java.util.Optional.empty());

        assertThatThrownBy(() -> educationService.getEducationById(1L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
