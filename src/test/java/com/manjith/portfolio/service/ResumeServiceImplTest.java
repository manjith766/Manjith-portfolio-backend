package com.manjith.portfolio.service;

import com.manjith.portfolio.entity.Resume;
import com.manjith.portfolio.exception.BusinessValidationException;
import com.manjith.portfolio.exception.ResourceNotFoundException;
import com.manjith.portfolio.mapper.ResumeMapper;
import com.manjith.portfolio.repository.ResumeRepository;
import com.manjith.portfolio.service.impl.ResumeServiceImpl;
import com.manjith.portfolio.storage.CloudinaryUploadResult;
import com.manjith.portfolio.storage.FileStorageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResumeServiceImplTest {

    @Mock
    private ResumeRepository resumeRepository;
    @Mock
    private ResumeMapper resumeMapper;
    @Mock
    private FileStorageService fileStorageService;

    @InjectMocks
    private ResumeServiceImpl resumeService;

    @Test
    void uploadResume_throwsBusinessValidationException_whenFileIsEmpty() {
        MultipartFile emptyFile = new MockMultipartFile("file", "resume.pdf", "application/pdf", new byte[0]);

        assertThatThrownBy(() -> resumeService.uploadResume(emptyFile))
                .isInstanceOf(BusinessValidationException.class);

        verify(fileStorageService, never()).upload(any(), any());
    }

    @Test
    void uploadResume_throwsBusinessValidationException_whenContentTypeIsNotPdf() {
        MultipartFile wrongType = new MockMultipartFile("file", "resume.docx",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document", "content".getBytes());

        assertThatThrownBy(() -> resumeService.uploadResume(wrongType))
                .isInstanceOf(BusinessValidationException.class);

        verify(fileStorageService, never()).upload(any(), any());
    }

    @Test
    void uploadResume_throwsBusinessValidationException_whenFileExceedsMaxSize() {
        byte[] oversized = new byte[6 * 1024 * 1024]; // 6MB > 5MB limit
        MultipartFile tooLarge = new MockMultipartFile("file", "resume.pdf", "application/pdf", oversized);

        assertThatThrownBy(() -> resumeService.uploadResume(tooLarge))
                .isInstanceOf(BusinessValidationException.class);

        verify(fileStorageService, never()).upload(any(), any());
    }

    @Test
    void uploadResume_incrementsVersion_whenActiveResumeExists() {
        MultipartFile validFile = new MockMultipartFile("file", "resume.pdf", "application/pdf", "content".getBytes());
        Resume existingActive = Resume.builder().id(1L).version(3).isActive(true).build();

        when(resumeRepository.findByIsActiveTrue()).thenReturn(Optional.of(existingActive));
        when(fileStorageService.upload(any(), any()))
                .thenReturn(new CloudinaryUploadResult("https://res.cloudinary.com/demo/resume.pdf", "portfolio/resume/abc123"));
        when(resumeRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        resumeService.uploadResume(validFile);

        verify(resumeRepository).save(argThat(resume -> resume.getVersion() == 4));
    }

    @Test
    void deleteVersion_throwsBusinessValidationException_whenVersionIsActive() {
        Resume activeResume = Resume.builder().id(1L).isActive(true).build();
        when(resumeRepository.findById(1L)).thenReturn(Optional.of(activeResume));

        assertThatThrownBy(() -> resumeService.deleteVersion(1L))
                .isInstanceOf(BusinessValidationException.class);

        verify(resumeRepository, never()).deleteById(any());
        verify(fileStorageService, never()).delete(any());
    }

    @Test
    void deleteVersion_throwsResourceNotFoundException_whenNotFound() {
        when(resumeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> resumeService.deleteVersion(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getActiveResume_throwsResourceNotFoundException_whenNoneActive() {
        when(resumeRepository.findByIsActiveTrue()).thenReturn(Optional.empty());

        assertThatThrownBy(resumeService::getActiveResume)
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
