package com.manjith.portfolio.service;

import com.manjith.portfolio.dto.request.CertificationRequestDTO;
import com.manjith.portfolio.exception.ResourceNotFoundException;
import com.manjith.portfolio.mapper.CertificationMapper;
import com.manjith.portfolio.repository.CertificationRepository;
import com.manjith.portfolio.service.impl.CertificationServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CertificationServiceImplTest {

    @Mock
    private CertificationRepository certificationRepository;
    @Mock
    private CertificationMapper certificationMapper;

    @InjectMocks
    private CertificationServiceImpl certificationService;

    @Test
    void getCertificationById_throwsResourceNotFoundException_whenNotFound() {
        when(certificationRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> certificationService.getCertificationById(1L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void updateCertification_throwsResourceNotFoundException_whenNotFound() {
        CertificationRequestDTO request = CertificationRequestDTO.builder()
                .title("AWS - Solutions Architecture Job Simulation")
                .issuer("AWS")
                .issueDate(LocalDate.of(2025, 6, 1))
                .displayOrder(0)
                .build();

        when(certificationRepository.findById(5L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> certificationService.updateCertification(5L, request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deleteCertification_throwsResourceNotFoundException_whenNotExists() {
        when(certificationRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> certificationService.deleteCertification(99L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(certificationRepository, never()).deleteById(any());
    }
}
