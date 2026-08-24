package com.manjith.portfolio.service;

import com.manjith.portfolio.dto.response.ResumeResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ResumeService {

    ResumeResponseDTO getActiveResume();

    List<ResumeResponseDTO> getAllVersions();

    ResumeResponseDTO uploadResume(MultipartFile file);

    void deleteVersion(Long id);
}
