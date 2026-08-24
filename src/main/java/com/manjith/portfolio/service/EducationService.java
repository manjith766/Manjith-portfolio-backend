package com.manjith.portfolio.service;

import com.manjith.portfolio.dto.request.EducationRequestDTO;
import com.manjith.portfolio.dto.response.EducationResponseDTO;

import java.util.List;

public interface EducationService {

    List<EducationResponseDTO> getAllEducation();

    EducationResponseDTO getEducationById(Long id);

    EducationResponseDTO createEducation(EducationRequestDTO requestDTO);

    EducationResponseDTO updateEducation(Long id, EducationRequestDTO requestDTO);

    void deleteEducation(Long id);
}
